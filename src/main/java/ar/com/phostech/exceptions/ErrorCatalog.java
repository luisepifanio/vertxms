package ar.com.phostech.exceptions;

import static java.net.HttpURLConnection.*;

public enum ErrorCatalog {


    UNKNOWN(HTTP_INTERNAL_ERROR, ErrorCodes.UNKNOWN),
    INTERNAL_SERVER_ERROR(HTTP_INTERNAL_ERROR, ErrorCodes.INTERNAL_ERROR),
    VALIDATION_FAILED(HTTP_BAD_REQUEST,ErrorCodes.VALIDATION_ERROR),
    OPERATION_FAILED(HTTP_BAD_REQUEST,ErrorCodes.OPERATION_FAILED),
    NOT_FOUND(HTTP_NOT_FOUND,ErrorCodes.NOT_FOUND)
    ;

    public final int status;
    public final String defaultMessage;
    public final String error;

    ErrorCatalog(int _status, ErrorCodes errorCode) {
        status = _status;
        defaultMessage = errorCode.message;
        error = errorCode.error;
    }

    public static ErrorCatalog from(String id) {
        for (ErrorCatalog anEnum : values()) {
            if (anEnum.name().equalsIgnoreCase(id)) {
                return anEnum;
            }
        }
        return null;
    }
}
