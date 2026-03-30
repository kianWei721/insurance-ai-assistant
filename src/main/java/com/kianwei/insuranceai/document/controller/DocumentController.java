package com.kianwei.insuranceai.document.controller;

import com.kianwei.insuranceai.common.result.Result;
import com.kianwei.insuranceai.common.result.ResultCode;
import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
import com.kianwei.insuranceai.orchestrator.DocumentOrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * API entry point for document ingestion requests. This controller currently
 * exposes only the upload stub needed to accept a document request shape before
 * parse, chunk, OCR, and embedding workflows are implemented.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentOrchestratorService documentOrchestratorService;

    /**
     * Accept an upload request and return the normalized document metadata that
     * would enter the ingestion pipeline. The stub validates the request shape
     * and responds with an {@link DocumentUploadResponse} wrapped in
     * {@link Result}, but it does not persist files, perform OCR, parse pages,
     * chunk text, or generate embeddings yet.
     *
     * @param documentType business document type supplied by the client
     * @param file multipart file payload to be uploaded
     * @return success result containing accepted upload metadata, or a failure
     *     result when the request is incomplete
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<DocumentUploadResponse> uploadDocument(
        @RequestParam("documentType") String documentType,
        @RequestParam("file") MultipartFile file) {

        if (!StringUtils.hasText(documentType)) {
            return Result.fail(ResultCode.BAD_REQUEST, "documentType is required");
        }

        if (file == null || file.isEmpty()) {
            return Result.fail(ResultCode.BAD_REQUEST, "file must not be empty");
        }

        return Result.success(
            "Document upload stub accepted. Persistence and downstream processing are not implemented yet.",
            documentOrchestratorService.handleUpload(documentType, file));
    }
}
