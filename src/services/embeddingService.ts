/**
 * EmbeddingService – generates a numeric vector for a piece of text.
 *
 * The real implementation calls an external provider (OpenAI, etc.).
 * Tests can swap in a mock via the interface.
 */

export interface EmbeddingService {
  embed(text: string): Promise<number[]>;
}

/**
 * Default implementation backed by the EMBEDDING_* environment variables.
 * Only OpenAI is wired up; other providers can be added later.
 */
export function createEmbeddingService(): EmbeddingService {
  const provider = process.env.EMBEDDING_PROVIDER || 'openai';
  const apiKey = process.env.EMBEDDING_API_KEY || '';
  const model = process.env.EMBEDDING_MODEL || 'text-embedding-3-small';

  if (provider !== 'openai') {
    throw new Error(`Unsupported embedding provider: ${provider}`);
  }

  if (!apiKey) {
    throw new Error('EMBEDDING_API_KEY environment variable is not set.');
  }

  return {
    async embed(text: string): Promise<number[]> {
      const response = await fetch('https://api.openai.com/v1/embeddings', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${apiKey}`,
        },
        body: JSON.stringify({ input: text, model }),
      });

      if (!response.ok) {
        const body = await response.text();
        throw new Error(`Embedding API error ${response.status}: ${body}`);
      }

      const json = (await response.json()) as {
        data: Array<{ embedding: number[] }>;
      };
      return json.data[0].embedding;
    },
  };
}
