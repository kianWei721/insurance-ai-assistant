package com.kianwei.insuranceai.document.service;

import com.kianwei.insuranceai.document.entity.InsuranceDocument;
import org.springframework.stereotype.Service;

/**
 * Service responsible for chunk records in the final stage of the upload ->
 * parse -> chunk workflow. The class is intentionally limited to base service
 * capabilities until chunking and embedding generation are introduced.
 */
@Service
public class InsuranceChunkService {

    /**
     * Reserve the chunk-processing step after parsing. Real chunk generation
     * will be attached here once page parsing produces structured content.
     *
     * @param document document aggregate that will eventually produce chunks
     */
    public void prepareForChunking(InsuranceDocument document) {
        // Chunking is intentionally deferred. The orchestrator calls this
        // method now so the final pipeline stage is visible and testable.
    }
}
