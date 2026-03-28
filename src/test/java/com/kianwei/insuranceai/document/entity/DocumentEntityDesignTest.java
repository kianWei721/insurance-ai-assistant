package com.kianwei.insuranceai.document.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Arrays;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DocumentEntityDesignTest {

    private static final Path MAIN_JAVA = Path.of(System.getProperty("user.dir"))
        .resolve("src/main/java/com/kianwei/insuranceai/document");

    @Test
    void documentPackagesArePresent() {
        List<String> subpackages = List.of("controller", "dto", "entity", "mapper", "service");

        for (String subpackage : subpackages) {
            Path packageInfo = MAIN_JAVA.resolve(subpackage).resolve("package-info.java");
            assertTrue(Files.exists(packageInfo), () -> "Missing package marker: " + packageInfo);
        }
    }

    @Test
    void insuranceDocumentDesignMatchesMvpPhaseOne() throws NoSuchFieldException {
        assertTableName(InsuranceDocument.class, "insurance_document");
        assertIdAndDeletedFields(InsuranceDocument.class);
        assertFieldNames(
            InsuranceDocument.class,
            List.of(
                "id",
                "documentType",
                "sourceFilename",
                "storageKey",
                "mimeType",
                "fileSizeBytes",
                "pageCount",
                "checksumSha256",
                "status",
                "uploadedAt",
                "createdAt",
                "updatedAt",
                "deleted"));
        assertEquals(DocumentStatus.class, InsuranceDocument.class.getDeclaredField("status").getType());
    }

    @Test
    void insuranceDocumentPageDesignMatchesMvpPhaseOne() throws NoSuchFieldException {
        assertTableName(InsuranceDocumentPage.class, "insurance_document_page");
        assertIdAndDeletedFields(InsuranceDocumentPage.class);
        assertFieldNames(
            InsuranceDocumentPage.class,
            List.of(
                "id",
                "documentId",
                "pageNo",
                "storageKey",
                "width",
                "height",
                "rotation",
                "createdAt",
                "updatedAt",
                "deleted"));
    }

    @Test
    void insuranceChunkDesignMatchesMvpPhaseOne() throws NoSuchFieldException {
        assertTableName(InsuranceChunk.class, "insurance_chunk");
        assertIdAndDeletedFields(InsuranceChunk.class);
        assertFieldNames(
            InsuranceChunk.class,
            List.of(
                "id",
                "documentId",
                "pageId",
                "pageNo",
                "chunkIndex",
                "content",
                "tokenCount",
                "embedding",
                "createdAt",
                "updatedAt",
                "deleted"));
        assertEquals(byte[].class, InsuranceChunk.class.getDeclaredField("embedding").getType());
    }

    @Test
    void documentStatusSupportsExpectedPhaseOneStates() {
        assertEquals(
            List.of(DocumentStatus.UPLOADED, DocumentStatus.PARSED, DocumentStatus.CHUNKED, DocumentStatus.FAILED),
            List.of(DocumentStatus.values()));
    }

    private static void assertTableName(Class<?> type, String expectedValue) {
        TableName tableName = type.getAnnotation(TableName.class);
        assertNotNull(tableName, () -> type.getSimpleName() + " should declare @TableName");
        assertEquals(expectedValue, tableName.value());
    }

    private static void assertIdAndDeletedFields(Class<?> type) throws NoSuchFieldException {
        assertNotNull(type.getDeclaredField("id").getAnnotation(TableId.class));
        assertNotNull(type.getDeclaredField("deleted").getAnnotation(TableLogic.class));
    }

    private static void assertFieldNames(Class<?> type, List<String> expectedFields) {
        List<String> actualFields = Arrays.stream(type.getDeclaredFields()).map(Field::getName).toList();
        assertEquals(Set.copyOf(expectedFields), Set.copyOf(actualFields));
        assertEquals(expectedFields.size(), actualFields.size());
    }
}
