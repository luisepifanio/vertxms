package ar.com.phostech.exceptions;

public enum ErrorCodes {

    UNKNOWN("unknown", "An unhandled error occurred"),
    INTERNAL_ERROR("error", "An internal error occurred"),
    VALIDATION_ERROR("validation_error", "A constraint validation was violated"),
    NOT_FOUND("not_found", "Resources was not found"),
    OPERATION_FAILED("operation_failed", "Operation failed");

    final String error;
    final String message;

    ErrorCodes(String _code, String _message) {
        this.error = _code;
        this.message = _message;
    }
}
