import { getDb, flushDb } from '../config/database';
import { Document } from '../models/document';

export interface DocumentRepository {
  save(doc: Document): Document & { id: number };
  findById(id: number): (Document & { id: number }) | undefined;
}

export function createDocumentRepository(): DocumentRepository {
  return {
    save(doc: Document) {
      const db = getDb();
      db._docSeq += 1;
      const record = { ...doc, id: db._docSeq };
      db.documents.push(record);
      flushDb();
      return record;
    },

    findById(id: number) {
      const db = getDb();
      return db.documents.find((d) => d.id === id);
    },
  };
}
