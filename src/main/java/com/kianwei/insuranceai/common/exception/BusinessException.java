package com.kianwei.insuranceai.common.exception;

import com.kianwei.insuranceai.common.result.ResultCode;
import lombok.Getter;

/**
 * Unchecked exception representing a predictable business-rule violation.
 * Throw this when the error should be surfaced to the caller with a specific
 * {@link ResultCode} rather than treated as an unexpected server error.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ResultCode resultCode;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public BusinessException(ResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }
}
