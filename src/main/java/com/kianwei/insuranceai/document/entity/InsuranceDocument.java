package com.kianwei.insuranceai.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Aggregate root for a source document as it moves through the upload, parse,
 * and chunk stages of the document ingestion pipeline.
 */
@Getter
@Setter
@TableName("insurance_document")
public class InsuranceDocument {

    /**
     * Primary key for the document aggregate used across all ingestion stages.
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Business-facing document classification such as policy, claim, or EOB.
     */
    @TableField("document_type")
    private String documentType;

    /**
     * Original filename received from the upload request for auditability.
     */
    @TableField("source_filename")
    private String sourceFilename;

    /**
     * Storage location key reserved for the persisted file object.
     */
    @TableField("storage_key")
    private String storageKey;

    /**
     * MIME type captured at upload time to validate downstream processing.
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * File size in bytes used for operational checks and storage monitoring.
     */
    @TableField("file_size_bytes")
    private Long fileSizeBytes;

    /**
     * Total number of parsed pages once page extraction is available.
     */
    @TableField("page_count")
    private Integer pageCount;

    /**
     * SHA-256 checksum used to detect duplicate uploads and integrity issues.
     */
    @TableField("checksum_sha256")
    private String checksumSha256;

    /**
     * Current lifecycle state in the upload -> parse -> chunk workflow.
     */
    @TableField("status")
    private DocumentStatus status;

    /**
     * Timestamp when the raw document was accepted by the upload endpoint.
     */
    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;

    /**
     * Timestamp when the database record was created.
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the document metadata was last updated.
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Logical deletion flag so document records remain recoverable for audit.
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
