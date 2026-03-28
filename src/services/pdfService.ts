import pdfParse from 'pdf-parse';
import crypto from 'crypto';
import fs from 'fs';
import { Chunk } from '../models/document';

export interface ChunkOptions {
  chunkSize?: number;  // characters per chunk (default 500)
  overlap?: number;    // character overlap between chunks (default 50)
}

export interface PdfService {
  extractText(buffer: Buffer): Promise<string>;
  splitIntoChunks(text: string, documentId: number, opts?: ChunkOptions): Chunk[];
  sha256(buffer: Buffer): string;
}

export function createPdfService(): PdfService {
  return {
    async extractText(buffer: Buffer): Promise<string> {
      const data = await pdfParse(buffer);
      return data.text;
    },

    splitIntoChunks(
      text: string,
      documentId: number,
      opts: ChunkOptions = {}
    ): Chunk[] {
      const chunkSize = opts.chunkSize ?? 500;
      const overlap = opts.overlap ?? 50;
      const chunks: Chunk[] = [];
      let start = 0;
      let index = 0;

      while (start < text.length) {
        const end = Math.min(start + chunkSize, text.length);
        chunks.push({
          document_id: documentId,
          chunk_index: index,
          text: text.slice(start, end),
          start_char: start,
          end_char: end,
        });
        index++;
        if (end === text.length) break;
        start = end - overlap;
      }

      return chunks;
    },

    sha256(buffer: Buffer): string {
      return crypto.createHash('sha256').update(buffer).digest('hex');
    },
  };
}
