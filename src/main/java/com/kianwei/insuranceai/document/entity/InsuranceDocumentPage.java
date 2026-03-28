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
 * Page-level metadata for a document.
 */
@Getter
@Setter
@TableName("insurance_document_page")
public class InsuranceDocumentPage {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("document_id")
    private Long documentId;

    @TableField("page_number")
    private Integer pageNumber;

    @TableField("storage_key")
    private String storageKey;

    @TableField("width")
    private Integer width;

    @TableField("height")
    private Integer height;

    @TableField("rotation")
    private Integer rotation;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
