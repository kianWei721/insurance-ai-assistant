package com.kianwei.insuranceai.orchestrator;

import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
import com.kianwei.insuranceai.document.entity.DocumentStatus;
import com.kianwei.insuranceai.document.entity.InsuranceDocument;
import com.kianwei.insuranceai.document.service.InsuranceChunkService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentPageService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for coordinating the document workflow from upload
 * acceptance through record creation and preparation for future parse and chunk
 * stages.
 *
 * <p>The workflow is intentionally explicit even though parsing and chunking
 * are not implemented yet:
 * <ol>
 *   <li>Accept the upload metadata and create the initial document record.</li>
 *   <li>Update the document status so downstream stages can track progress.</li>
 *   <li>Invoke the page service hook where future parsing will begin.</li>
 *   <li>Invoke the chunk service hook where future chunking will begin.</li>
 *   <li>Return the upload response that describes the current pipeline state.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
public class DocumentOrchestratorService {

    private final InsuranceDocumentService insuranceDocumentService;
    private final InsuranceDocumentPageService insuranceDocumentPageService;
    private final InsuranceChunkService insuranceChunkService;

    /**
     * Coordinate the upload -> store -> parse -> chunk workflow for a single
     * document request. The current implementation creates the document record,
     * marks it as uploaded, and calls the parse/chunk preparation hooks without
     * performing actual parsing or chunk generation yet.
     *
     * @param documentType business document type supplied by the caller
     * @param file uploaded multipart payload
     * @return response metadata representing the accepted upload workflow state
     */
    public DocumentUploadResponse handleUpload(String documentType, MultipartFile file) {
        InsuranceDocument document = insuranceDocumentService.createDocumentRecord(documentType, file);

        try {
            insuranceDocumentService.updateDocumentStatus(document, DocumentStatus.UPLOADED);
            insuranceDocumentPageService.prepareForParsing(document);
            insuranceChunkService.prepareForChunking(document);
            return insuranceDocumentService.buildUploadResponse(document);
        } catch (RuntimeException exception) {
            insuranceDocumentService.updateDocumentStatus(document, DocumentStatus.FAILED);
            throw exception;
        }
    }
}
