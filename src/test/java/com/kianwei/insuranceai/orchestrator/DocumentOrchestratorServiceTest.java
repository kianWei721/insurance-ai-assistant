package com.kianwei.insuranceai.orchestrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
import com.kianwei.insuranceai.document.entity.DocumentStatus;
import com.kianwei.insuranceai.document.entity.InsuranceDocument;
import com.kianwei.insuranceai.document.service.InsuranceChunkService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentPageService;
import com.kianwei.insuranceai.document.service.InsuranceDocumentService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

/**
 * Unit tests for the workflow coordinator that bridges upload acceptance with
 * future parsing and chunking stages.
 */
@ExtendWith(MockitoExtension.class)
class DocumentOrchestratorServiceTest {

    @Mock
    private InsuranceDocumentService insuranceDocumentService;

    @Mock
    private InsuranceDocumentPageService insuranceDocumentPageService;

    @Mock
    private InsuranceChunkService insuranceChunkService;

    @InjectMocks
    private DocumentOrchestratorService documentOrchestratorService;

    @Test
    void handleUploadCoordinatesDocumentWorkflow() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "policy.pdf",
            "application/pdf",
            "sample".getBytes());
        InsuranceDocument document = new InsuranceDocument();
        document.setStatus(DocumentStatus.UPLOADED);
        document.setUploadedAt(LocalDateTime.of(2026, 3, 28, 16, 0));

        DocumentUploadResponse response = DocumentUploadResponse.builder()
            .sourceFilename("policy.pdf")
            .documentType("POLICY")
            .mimeType("application/pdf")
            .fileSizeBytes(6L)
            .status(DocumentStatus.UPLOADED)
            .uploadedAt(LocalDateTime.of(2026, 3, 28, 16, 0))
            .build();

        when(insuranceDocumentService.createDocumentRecord("POLICY", file)).thenReturn(document);
        when(insuranceDocumentService.buildUploadResponse(document)).thenReturn(response);

        DocumentUploadResponse actual = documentOrchestratorService.handleUpload("POLICY", file);

        assertSame(response, actual);
        InOrder inOrder = inOrder(
            insuranceDocumentService,
            insuranceDocumentPageService,
            insuranceChunkService);
        inOrder.verify(insuranceDocumentService).createDocumentRecord("POLICY", file);
        inOrder.verify(insuranceDocumentService).updateDocumentStatus(document, DocumentStatus.UPLOADED);
        inOrder.verify(insuranceDocumentPageService).prepareForParsing(document);
        inOrder.verify(insuranceChunkService).prepareForChunking(document);
        inOrder.verify(insuranceDocumentService).buildUploadResponse(document);
    }

    @Test
    void handleUploadMarksDocumentFailedWhenPreparationFails() {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "policy.pdf",
            "application/pdf",
            "sample".getBytes());
        InsuranceDocument document = new InsuranceDocument();
        RuntimeException failure = new RuntimeException("parse hook unavailable");

        when(insuranceDocumentService.createDocumentRecord("POLICY", file)).thenReturn(document);
        doThrow(failure).when(insuranceDocumentPageService).prepareForParsing(same(document));

        RuntimeException actual = assertThrows(
            RuntimeException.class,
            () -> documentOrchestratorService.handleUpload("POLICY", file));

        assertSame(failure, actual);
        verify(insuranceDocumentService).updateDocumentStatus(document, DocumentStatus.UPLOADED);
        verify(insuranceDocumentService).updateDocumentStatus(document, DocumentStatus.FAILED);
    }

    @Test
    void documentOrchestratorServiceIsRegisteredAsService() {
        assertEquals(Service.class, DocumentOrchestratorService.class.getAnnotation(Service.class).annotationType());
    }
}
