package com.kianwei.insuranceai.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kianwei.insuranceai.document.entity.InsuranceDocumentPage;

/**
 * Persistence gateway for page records that sit between documents and chunks in
 * the ingestion pipeline.
 */
public interface InsuranceDocumentPageMapper extends BaseMapper<InsuranceDocumentPage> {
}
