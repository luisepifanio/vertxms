package ar.com.phostech.demo.exceptions

import ar.com.phostech.vertx.response.ErrorDetail
import java.io.Serializable
import java.net.HttpURLConnection

enum class ErrorCatalog
(
    val httpStatus: Int,
    val errorCode: String,
    val defaultMessage: String
) : Serializable {
    // 200 OK
    OPERATION_IN_PROGRESS(HttpURLConnection.HTTP_CREATED, "operation.running", ""),
    // 400 CLIENT FAILED
    REQUIRED_ARGUMENT(HttpURLConnection.HTTP_BAD_REQUEST, "required.field", "Field must not be null"),
    INVALID_STATUS(HttpURLConnection.HTTP_BAD_REQUEST, "invalid.status", "Invalid order status."),
    VALIDATION_FAILED(HttpURLConnection.HTTP_BAD_REQUEST, "validation.failed", "Validation Failed"),
    // 500 Failed
    UNKNOWN(HttpURLConnection.HTTP_INTERNAL_ERROR, "unknown.error", "Unknown error"),
    METHOD_NOT_IMPLEMENTED(HttpURLConnection.HTTP_NOT_IMPLEMENTED, "not.implemented", "Method not implemented"),
    ;

    fun asErrorDetail(): ErrorDetail {
        return ErrorDetail(
            errorCode,
            defaultMessage
        )
    }

}

