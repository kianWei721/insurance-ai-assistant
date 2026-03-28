package com.kianwei.insuranceai.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kianwei.insuranceai.document.entity.InsuranceDocument;

/**
 * Persistence gateway for document aggregate records accepted by the ingestion
 * pipeline.
 */
public interface InsuranceDocumentMapper extends BaseMapper<InsuranceDocument> {
}
