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
 * Aggregate root for an uploaded insurance document.
 */
@Getter
@Setter
@TableName("insurance_document")
public class InsuranceDocument {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("document_type")
    private String documentType;

    @TableField("source_filename")
    private String sourceFilename;

    @TableField("storage_key")
    private String storageKey;

    @TableField("mime_type")
    private String mimeType;

    @TableField("file_size_bytes")
    private Long fileSizeBytes;

    @TableField("page_count")
    private Integer pageCount;

    @TableField("checksum_sha256")
    private String checksumSha256;

    @TableField("status")
    private DocumentStatus status;

    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
