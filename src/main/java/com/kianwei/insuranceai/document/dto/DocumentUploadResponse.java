package com.kianwei.insuranceai.document.dto;

import com.kianwei.insuranceai.document.entity.DocumentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * API response returned by the upload stub to describe the accepted document
 * metadata before parse and chunk processing are implemented.
 */
@Getter
@Builder
public class DocumentUploadResponse {

    /**
     * Original filename received from the client upload request.
     */
    private final String sourceFilename;

    /**
     * Business-facing document type supplied by the caller.
     */
    private final String documentType;

    /**
     * MIME type reported by the uploaded file payload.
     */
    private final String mimeType;

    /**
     * File size in bytes reported by the uploaded file payload.
     */
    private final Long fileSizeBytes;

    /**
     * Lifecycle status assigned when the upload is accepted by the stub.
     */
    private final DocumentStatus status;

    /**
     * Timestamp returned by the stub to indicate when the request was accepted.
     */
    private final LocalDateTime uploadedAt;
}
