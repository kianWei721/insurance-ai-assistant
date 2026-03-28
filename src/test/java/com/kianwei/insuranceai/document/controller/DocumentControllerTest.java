package com.kianwei.insuranceai.document.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kianwei.insuranceai.document.dto.DocumentUploadResponse;
import com.kianwei.insuranceai.document.entity.DocumentStatus;
import com.kianwei.insuranceai.document.service.InsuranceDocumentService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Focused MVC test for the document upload stub contract without involving OCR,
 * chunking, persistence, or application-wide infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private InsuranceDocumentService insuranceDocumentService;

    @InjectMocks
    private DocumentController documentController;

    @Test
    void uploadDocumentReturnsResultWrappedStubMetadata() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "policy.pdf",
            "application/pdf",
            "sample".getBytes());

        DocumentUploadResponse response = DocumentUploadResponse.builder()
            .sourceFilename("policy.pdf")
            .documentType("POLICY")
            .mimeType("application/pdf")
            .fileSizeBytes(6L)
            .status(DocumentStatus.UPLOADED)
            .uploadedAt(LocalDateTime.of(2026, 3, 28, 15, 30))
            .build();

        when(insuranceDocumentService.buildUploadStubResponse(eq("POLICY"), eq(file))).thenReturn(response);

        mockMvc.perform(multipart("/documents/upload")
                .file(file)
                .param("documentType", "POLICY"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value(
                "Document upload stub accepted. Persistence and downstream processing are not implemented yet."))
            .andExpect(jsonPath("$.data.sourceFilename").value("policy.pdf"))
            .andExpect(jsonPath("$.data.status").value("UPLOADED"));

        verify(insuranceDocumentService).buildUploadStubResponse("POLICY", file);
    }

    @Test
    void uploadDocumentRejectsEmptyFile() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/documents/upload")
                .file(file)
                .param("documentType", "POLICY"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("file must not be empty"));
    }
}
