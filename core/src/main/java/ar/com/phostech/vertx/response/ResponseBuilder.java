package ar.com.phostech.vertx.response;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResponseBuilder {

    public static <T> FailedResponse<T> fromThrowable(Throwable throwable) {
        return fromThrowable(
                500,
                throwable.toString().replace("[\r?\n]+", " "),
                throwable);
    }

    public static <T> FailedResponse<T> fromThrowable(int status, Throwable throwable) {
        return fromThrowable(
                status,
                throwable.toString().replace("[\r?\n]+", " "),
                throwable);
    }

    public static <T> FailedResponse<T> fromFailure(int status, String message) {
        return new FailedResponse<>(status, message);
    }

    public static <T> FailedResponse<T> fromFailure(int status, String message, List<ErrorDetail> details) {
        return new FailedResponse<>(
                status,
                message,
                Collections.emptyList(),
                details
        );
    }

    public static <T> FailedResponse<T> fromThrowable(int status, String message, Throwable throwable) {
        return new FailedResponse<>(
                status,
                message,
                Stream.of(throwable.getStackTrace())
                        .map(StackTraceElement::toString)
                        .collect(Collectors.toList()),
                Collections.emptyList()
        );
    }

    public static <T> Response<T> from(T body) {
        return new GenericResponse<>(body);
    }
}
