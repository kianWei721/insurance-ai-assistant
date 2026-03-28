import { Request, Response } from 'express';
import path from 'path';
import { createDocumentRepository } from '../repositories/documentRepository';
import { createChunkRepository } from '../repositories/chunkRepository';
import { createPdfService } from '../services/pdfService';
import { createEmbeddingService, EmbeddingService } from '../services/embeddingService';

// Allow injection of services (useful for testing / DI)
export function createDocumentController(
  embeddingService: EmbeddingService = createEmbeddingService()
) {
  const docRepo = createDocumentRepository();
  const chunkRepo = createChunkRepository();
  const pdfService = createPdfService();

  return {
    /**
     * POST /api/documents/pdf/import
     * multipart/form-data, field: file
     */
    async importPdf(req: Request, res: Response): Promise<void> {
      try {
        const file = req.file;
        if (!file) {
          res.status(400).json({ error: 'No file uploaded.' });
          return;
        }

        // Validate MIME type
        if (file.mimetype !== 'application/pdf') {
          res.status(415).json({ error: 'Only PDF files are accepted.' });
          return;
        }

        const buffer = file.buffer;

        // Validate magic bytes (%PDF-)
        if (
          buffer.length < 5 ||
          buffer.toString('ascii', 0, 5) !== '%PDF-'
        ) {
          res.status(415).json({ error: 'File does not appear to be a valid PDF.' });
          return;
        }

        // 1. Save document metadata
        const sha256 = pdfService.sha256(buffer);
        const storagePath = path.join(
          'uploads',
          `${sha256}${path.extname(file.originalname)}`
        );

        const savedDoc = docRepo.save({
          original_name: file.originalname,
          size: file.size,
          mime_type: file.mimetype,
          uploaded_at: new Date().toISOString(),
          sha256,
          storage_path: storagePath,
        });

        // 2. Extract text and split into chunks
        const text = await pdfService.extractText(buffer);
        const rawChunks = pdfService.splitIntoChunks(text, savedDoc.id);

        // 3. Generate embeddings for each chunk
        const chunks = await Promise.all(
          rawChunks.map(async (chunk) => {
            const vector = await embeddingService.embed(chunk.text);
            return { ...chunk, embedding: JSON.stringify(vector) };
          })
        );

        // 4. Persist chunks with vectors
        chunkRepo.saveMany(chunks);

        res.status(201).json({
          documentId: savedDoc.id,
          fileId: savedDoc.id,
          chunkCount: chunks.length,
          status: 'ok',
        });
      } catch (err) {
        const message = err instanceof Error ? err.message : 'Internal server error';
        res.status(500).json({ error: message });
      }
    },
  };
}
