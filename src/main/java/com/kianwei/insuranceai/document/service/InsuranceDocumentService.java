package com.kianwei.insuranceai.document.service;

import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
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
     * Build a response payload for the upload stub after basic request metadata
     * has been accepted.
     *
     * @param documentType business document type supplied by the caller
     * @param file uploaded multipart payload
     * @return response metadata representing the accepted upload request
     */
    public DocumentUploadResponse buildUploadStubResponse(String documentType, MultipartFile file) {
        return DocumentUploadResponse.builder()
            .sourceFilename(resolveSourceFilename(file))
            .documentType(normalizeDocumentType(documentType))
            .mimeType(file.getContentType())
            .fileSizeBytes(file.getSize())
            .status(DocumentStatus.UPLOADED)
            .uploadedAt(LocalDateTime.now())
            .build();
    }

    private String normalizeDocumentType(String documentType) {
        return documentType == null ? "" : documentType.strip();
    }

    private String resolveSourceFilename(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        return StringUtils.hasText(filename) ? filename : "unknown";
    }
}
