export interface Document {
  id?: number;
  original_name: string;
  size: number;
  mime_type: string;
  uploaded_at: string;
  sha256: string;
  storage_path: string;
}

export interface Chunk {
  id?: number;
  document_id: number;
  chunk_index: number;
  text: string;
  start_char: number;
  end_char: number;
  page_hint?: number;
  embedding?: string; // JSON-serialised number[]
}
