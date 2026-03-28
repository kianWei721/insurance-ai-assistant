package com.kianwei.insuranceai.common.result;

import lombok.Getter;

/**
 * Standardized response code enumeration.
 * <p>
 * Convention: 2xxxx = success, 4xxxx = client error, 5xxxx = server error.
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "Success"),

    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    METHOD_NOT_ALLOWED(405, "Method not allowed"),

    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    SERVICE_UNAVAILABLE(503, "Service unavailable");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
