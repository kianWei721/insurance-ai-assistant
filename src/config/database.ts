/**
 * Simple JSON-file-based store.
 *
 * All data lives in a single JSON file (configurable via DATABASE_PATH).
 * The store is kept in memory during a process lifetime; writes flush to disk.
 *
 * Using `:memory:` as DATABASE_PATH disables disk persistence – useful in tests.
 */

import fs from 'fs';
import path from 'path';
import { Document, Chunk } from '../models/document';

export interface DbStore {
  documents: Array<Document & { id: number }>;
  chunks: Array<Chunk & { id: number }>;
  _docSeq: number;
  _chunkSeq: number;
}

let store: DbStore | null = null;
let storePath: string;

function resolvedPath(): string {
  return process.env.DATABASE_PATH || './data/insurance_ai.json';
}

export function getDb(): DbStore {
  if (!store) {
    storePath = resolvedPath();
    if (storePath === ':memory:') {
      store = emptyStore();
    } else {
      const dir = path.dirname(storePath);
      if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true });
      }
      if (fs.existsSync(storePath)) {
        store = JSON.parse(fs.readFileSync(storePath, 'utf8')) as DbStore;
      } else {
        store = emptyStore();
      }
    }
  }
  return store;
}

export function flushDb(): void {
  if (store && storePath !== ':memory:') {
    fs.writeFileSync(storePath, JSON.stringify(store, null, 2), 'utf8');
  }
}

/** Reset the in-memory singleton – useful between tests. */
export function closeDb(): void {
  store = null;
}

function emptyStore(): DbStore {
  return { documents: [], chunks: [], _docSeq: 0, _chunkSeq: 0 };
}
