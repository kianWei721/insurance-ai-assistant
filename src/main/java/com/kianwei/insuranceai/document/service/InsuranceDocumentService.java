package com.kianwei.insuranceai.document.service;

import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
import com.kianwei.insuranceai.document.entity.InsuranceDocument;
import com.kianwei.insuranceai.document.entity.DocumentStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service responsible for the document aggregate in the upload -> parse ->
 * chunk workflow. At this stage it prepares upload response metadata without
 * performing OCR, persistence orchestration, chunking, or embedding logic.
 */
@Service
public class InsuranceDocumentService {

    /**
     * Create the initial document aggregate for the upload -> store -> parse ->
     * chunk workflow. The record captures the accepted upload metadata and is
     * ready for persistence integration in a later iteration.
     *
     * @param documentType business document type supplied by the caller
     * @param file uploaded multipart payload
     * @return document aggregate prepared for orchestration
     */
    public InsuranceDocument createDocumentRecord(String documentType, MultipartFile file) {
        LocalDateTime acceptedAt = LocalDateTime.now();

        InsuranceDocument document = new InsuranceDocument();
        document.setSourceFilename(resolveSourceFilename(file));
        document.setDocumentType(normalizeDocumentType(documentType));
        document.setMimeType(file.getContentType());
        document.setFileSizeBytes(file.getSize());
        document.setStatus(DocumentStatus.UPLOADED);
        document.setUploadedAt(acceptedAt);
        document.setCreatedAt(acceptedAt);
        document.setUpdatedAt(acceptedAt);
        document.setDeleted(0);
        return document;
    }

    /**
     * Update the lifecycle status of the document aggregate while the
     * orchestrator advances through upload, parse, and chunk stages.
     *
     * @param document document aggregate under orchestration
     * @param status new lifecycle status to record
     */
    public void updateDocumentStatus(InsuranceDocument document, DocumentStatus status) {
        document.setStatus(status);
        document.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Build the upload response returned to API callers from the current
     * document aggregate state.
     *
     * @param document document aggregate created during orchestration
     * @return response metadata representing the accepted upload request
     */
    public DocumentUploadResponse buildUploadResponse(InsuranceDocument document) {
        return DocumentUploadResponse.builder()
            .sourceFilename(document.getSourceFilename())
            .documentType(document.getDocumentType())
            .mimeType(document.getMimeType())
            .fileSizeBytes(document.getFileSizeBytes())
            .status(document.getStatus())
            .uploadedAt(document.getUploadedAt())
            .build();
    }

    /**
     * Build a response payload for the upload stub after basic request metadata
     * has been accepted.
     *
     * @param documentType business document type supplied by the caller
     * @param file uploaded multipart payload
     * @return response metadata representing the accepted upload request
     */
    public DocumentUploadResponse buildUploadStubResponse(String documentType, MultipartFile file) {
        return buildUploadResponse(createDocumentRecord(documentType, file));
    }

    private String normalizeDocumentType(String documentType) {
        return documentType == null ? "" : documentType.strip();
    }

    private String resolveSourceFilename(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        return StringUtils.hasText(filename) ? filename : "unknown";
    }
}
