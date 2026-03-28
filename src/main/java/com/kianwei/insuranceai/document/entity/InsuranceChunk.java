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
 * Chunk-level record representing page-derived text units that will later feed
 * downstream parsing, chunking, and embedding workflows.
 */
@Getter
@Setter
@TableName("insurance_chunk")
public class InsuranceChunk {

    /**
     * Primary key for the chunk record.
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Parent document reference so chunks can be queried at document level.
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * Parent page reference that completes the Document -> Page -> Chunk chain.
     */
    @TableField("page_id")
    private Long pageId;

    /**
     * Human-readable page number copied from the parent page for filtering and
     * troubleshooting without requiring an additional join.
     */
    @TableField("page_no")
    private Integer pageNo;

    /**
     * Zero-based sequence of the chunk within a page so chunk order is stable
     * after parsing and chunk generation are implemented.
     */
    @TableField("chunk_index")
    private Integer chunkIndex;

    /**
     * Plain text content assigned to this chunk after page text extraction.
     */
    @TableField("content")
    private String content;

    /**
     * Token count placeholder reserved for future embedding preparation and
     * chunk-size governance once tokenization is introduced.
     */
    @TableField("token_count")
    private Integer tokenCount;

    /**
     * Optional binary placeholder for a future embedding payload. The field is
     * intentionally present so the schema can evolve without implementing any
     * vector generation or retrieval logic in this phase.
     */
    @TableField("embedding")
    private byte[] embedding;

    /**
     * Timestamp when the chunk record was created.
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * Timestamp when the chunk metadata was last updated.
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * Logical deletion flag for chunk records retained for audit and recovery.
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
