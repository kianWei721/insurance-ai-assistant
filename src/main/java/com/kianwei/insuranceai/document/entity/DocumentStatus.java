package com.kianwei.insuranceai.document.entity;

/**
 * Lifecycle status of a document in the phase-1 processing pipeline.
 */
public enum DocumentStatus {
    UPLOADED,
    PARSED,
    CHUNKED,
    FAILED
}
