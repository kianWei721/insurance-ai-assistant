/**
 * Unit tests for the PDF import pipeline.
 *
 * Strategy:
 *  - Use an in-memory SQLite database for each test (DATABASE_PATH = :memory:)
 *  - Mock pdfParse so no real PDF file is needed
 *  - Mock the embeddingService so no external API is called
 *  - Verify: chunking logic, embedding called per chunk, chunks persisted
 */

import { createPdfService } from '../src/services/pdfService';
import { createChunkRepository } from '../src/repositories/chunkRepository';
import { createDocumentRepository } from '../src/repositories/documentRepository';
import { EmbeddingService } from '../src/services/embeddingService';
import { closeDb } from '../src/config/database';

// Use an in-memory database for every test run
beforeAll(() => {
  process.env.DATABASE_PATH = ':memory:';
});

afterEach(() => {
  // Reset the singleton so each test gets a fresh DB
  closeDb();
});

// ---------------------------------------------------------------------------
// 1. splitIntoChunks – pure logic, no DB involved
// ---------------------------------------------------------------------------
describe('PdfService.splitIntoChunks', () => {
  const pdfService = createPdfService();

  it('splits text into chunks of the configured size', () => {
    const text = 'a'.repeat(1200);
    const chunks = pdfService.splitIntoChunks(text, 1, { chunkSize: 500, overlap: 0 });

    expect(chunks).toHaveLength(3);
    expect(chunks[0].text).toHaveLength(500);
    expect(chunks[1].text).toHaveLength(500);
    expect(chunks[2].text).toHaveLength(200);
  });

  it('applies overlap between consecutive chunks', () => {
    const text = 'abcdefghij'; // 10 chars
    const chunks = pdfService.splitIntoChunks(text, 1, { chunkSize: 6, overlap: 2 });

    // chunk 0: [0,6) → "abcdef"
    // chunk 1: [4,10) → "efghij"
    expect(chunks).toHaveLength(2);
    expect(chunks[0].text).toBe('abcdef');
    expect(chunks[1].text).toBe('efghij');
    expect(chunks[1].start_char).toBe(4);
  });

  it('returns a single chunk when text is shorter than chunkSize', () => {
    const text = 'short text';
    const chunks = pdfService.splitIntoChunks(text, 1, { chunkSize: 500, overlap: 50 });

    expect(chunks).toHaveLength(1);
    expect(chunks[0].text).toBe('short text');
    expect(chunks[0].chunk_index).toBe(0);
    expect(chunks[0].document_id).toBe(1);
  });

  it('assigns sequential chunk_index values', () => {
    const text = 'x'.repeat(1500);
    const chunks = pdfService.splitIntoChunks(text, 99, { chunkSize: 500, overlap: 0 });

    chunks.forEach((c, i) => {
      expect(c.chunk_index).toBe(i);
    });
  });

  it('records correct start_char and end_char', () => {
    const text = 'hello world extra';
    const chunks = pdfService.splitIntoChunks(text, 1, { chunkSize: 5, overlap: 0 });

    expect(chunks[0].start_char).toBe(0);
    expect(chunks[0].end_char).toBe(5);
    expect(chunks[1].start_char).toBe(5);
  });
});

// ---------------------------------------------------------------------------
// 2. sha256 helper
// ---------------------------------------------------------------------------
describe('PdfService.sha256', () => {
  const pdfService = createPdfService();

  it('returns a 64-character hex string', () => {
    const hash = pdfService.sha256(Buffer.from('test'));
    expect(hash).toMatch(/^[0-9a-f]{64}$/);
  });

  it('is deterministic', () => {
    const buf = Buffer.from('same input');
    expect(pdfService.sha256(buf)).toBe(pdfService.sha256(buf));
  });
});

// ---------------------------------------------------------------------------
// 3. DocumentRepository
// ---------------------------------------------------------------------------
describe('DocumentRepository', () => {
  it('saves and retrieves a document by id', () => {
    const repo = createDocumentRepository();
    const saved = repo.save({
      original_name: 'test.pdf',
      size: 1024,
      mime_type: 'application/pdf',
      uploaded_at: new Date().toISOString(),
      sha256: 'abc123',
      storage_path: 'uploads/abc123.pdf',
    });

    expect(saved.id).toBeGreaterThan(0);
    const fetched = repo.findById(saved.id);
    expect(fetched).toBeDefined();
    expect(fetched!.original_name).toBe('test.pdf');
    expect(fetched!.sha256).toBe('abc123');
  });
});

// ---------------------------------------------------------------------------
// 4. ChunkRepository
// ---------------------------------------------------------------------------
describe('ChunkRepository', () => {
  it('saves many chunks and retrieves them by documentId', () => {
    // Insert a document first (FK constraint)
    const docRepo = createDocumentRepository();
    const doc = docRepo.save({
      original_name: 'doc.pdf',
      size: 512,
      mime_type: 'application/pdf',
      uploaded_at: new Date().toISOString(),
      sha256: 'deadbeef',
      storage_path: 'uploads/deadbeef.pdf',
    });

    const pdfService = createPdfService();
    const text = 'hello world this is a test document for chunking';
    const chunks = pdfService.splitIntoChunks(text, doc.id, { chunkSize: 10, overlap: 0 });

    const chunkRepo = createChunkRepository();
    chunkRepo.saveMany(chunks.map((c) => ({ ...c, embedding: JSON.stringify([0.1, 0.2]) })));

    const saved = chunkRepo.findByDocumentId(doc.id);
    expect(saved).toHaveLength(chunks.length);
    expect(saved[0].document_id).toBe(doc.id);
    expect(JSON.parse(saved[0].embedding!)).toEqual([0.1, 0.2]);
  });
});

// ---------------------------------------------------------------------------
// 5. End-to-end pipeline with mocked embedding & DB
// ---------------------------------------------------------------------------
describe('PDF import pipeline (mocked embedding)', () => {
  it('calls embeddingService once per chunk and persists all chunks', async () => {
    // Mock embedding service
    const mockEmbed = jest.fn<Promise<number[]>, [string]>().mockResolvedValue([0.5, 0.6, 0.7]);
    const mockEmbeddingService: EmbeddingService = { embed: mockEmbed };

    // Prepare document and text
    const docRepo = createDocumentRepository();
    const savedDoc = docRepo.save({
      original_name: 'policy.pdf',
      size: 2048,
      mime_type: 'application/pdf',
      uploaded_at: new Date().toISOString(),
      sha256: 'feedface',
      storage_path: 'uploads/feedface.pdf',
    });

    const text = 'chunk1chunk2chunk3'; // 18 chars → 3 chunks of 6
    const pdfService = createPdfService();
    const rawChunks = pdfService.splitIntoChunks(text, savedDoc.id, {
      chunkSize: 6,
      overlap: 0,
    });

    // Call embedding for each chunk (as the controller does)
    const chunks = await Promise.all(
      rawChunks.map(async (chunk) => {
        const vector = await mockEmbeddingService.embed(chunk.text);
        return { ...chunk, embedding: JSON.stringify(vector) };
      })
    );

    // Persist
    const chunkRepo = createChunkRepository();
    chunkRepo.saveMany(chunks);

    // Assertions
    expect(mockEmbed).toHaveBeenCalledTimes(3);
    expect(mockEmbed).toHaveBeenNthCalledWith(1, 'chunk1');
    expect(mockEmbed).toHaveBeenNthCalledWith(2, 'chunk2');
    expect(mockEmbed).toHaveBeenNthCalledWith(3, 'chunk3');

    const persisted = chunkRepo.findByDocumentId(savedDoc.id);
    expect(persisted).toHaveLength(3);
    persisted.forEach((c) => {
      expect(JSON.parse(c.embedding!)).toEqual([0.5, 0.6, 0.7]);
    });
  });
});
