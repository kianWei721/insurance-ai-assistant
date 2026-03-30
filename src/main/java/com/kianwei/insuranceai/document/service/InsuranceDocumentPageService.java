package com.kianwei.insuranceai.document.service;

import com.kianwei.insuranceai.document.entity.InsuranceDocument;
import org.springframework.stereotype.Service;

/**
 * Service responsible for page records that will bridge raw documents and
 * future chunk generation once parsing is implemented.
 */
@Service
public class InsuranceDocumentPageService {

    /**
     * Reserve the page-processing step in the upload pipeline. Page extraction
     * and parsing will be added here when the parsing stage is implemented.
     *
     * @param document document aggregate that will eventually be parsed into pages
     */
    public void prepareForParsing(InsuranceDocument document) {
        // Parsing is intentionally deferred. The orchestrator calls this method
        // so the future page extraction handoff is explicit in the workflow.
    }
}
