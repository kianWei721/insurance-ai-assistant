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
 * Text chunk derived from a document page for downstream processing.
 */
@Getter
@Setter
@TableName("insurance_chunk")
public class InsuranceChunk {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("document_id")
    private Long documentId;

    @TableField("page_id")
    private Long pageId;

    @TableField("page_no")
    private Integer pageNo;

    @TableField("chunk_index")
    private Integer chunkIndex;

    @TableField("content")
    private String content;

    @TableField("token_count")
    private Integer tokenCount;

    @TableField("embedding")
    private byte[] embedding;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
