package com.kianwei.insuranceai.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kianwei.insuranceai.document.entity.InsuranceChunk;

/**
 * Persistence gateway for chunk records produced after page-level text is split
 * into stable retrieval-sized units.
 */
public interface InsuranceChunkMapper extends BaseMapper<InsuranceChunk> {
}
