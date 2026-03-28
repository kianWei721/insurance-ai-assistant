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
 * Page-level record that keeps document structure explicit between the uploaded
 * document aggregate and the chunks produced from each page.
 */
@Getter
@Setter
@TableName("insurance_document_page")
public class InsuranceDocumentPage {

    /**
     * Primary key for the page record within the ingestion pipeline.
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Foreign-key style reference to the parent insurance document.
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * One-based page number used to preserve human-readable page ordering.
     */
    @TableField("page_no")
    private Integer pageNo;

    /**
     * Storage key reserved for a page-level artifact if one is produced later.
     */
    @TableField("storage_key")
    private String storageKey;

    /**
     * Page width metadata for rendering or layout-aware parsing in the future.
     */
    @TableField("width")
    private Integer width;

    /**
     * Page height metadata for rendering or layout-aware parsing in the future.
     */
    @TableField("height")
    private Integer height;

    /**
     * Rotation angle captured so downstream parsing can normalize orientation.
     */
    @TableField("rotation")
    private Integer rotation;

    /**
     * Timestamp when the page record was created.
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the page metadata was last updated.
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Logical deletion flag for page records retained for operational audit.
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
