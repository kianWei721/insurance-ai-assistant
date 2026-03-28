import { getDb, flushDb } from '../config/database';
import { Chunk } from '../models/document';

export interface ChunkRepository {
  saveMany(chunks: Chunk[]): void;
  findByDocumentId(documentId: number): Array<Chunk & { id: number }>;
}

export function createChunkRepository(): ChunkRepository {
  return {
    saveMany(chunks: Chunk[]) {
      const db = getDb();
      for (const chunk of chunks) {
        db._chunkSeq += 1;
        db.chunks.push({ ...chunk, id: db._chunkSeq });
      }
      flushDb();
    },

    findByDocumentId(documentId: number) {
      const db = getDb();
      return db.chunks
        .filter((c) => c.document_id === documentId)
        .sort((a, b) => a.chunk_index - b.chunk_index);
    },
  };
}
