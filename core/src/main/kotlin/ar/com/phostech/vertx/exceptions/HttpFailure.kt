package ar.com.phostech.vertx.exceptions

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.impl.NoStackTraceThrowable
import java.util.Optional.ofNullable


class HttpFailure : NoStackTraceThrowable {

    val status: HttpResponseStatus
    val silent: Boolean

    constructor(_message: String, _status: HttpResponseStatus, _silent: Boolean)
        : super(
        ofNullable(_message)
            .filter({ value -> !value.trim().isEmpty() })
            .orElseGet(_status::reasonPhrase)
    ) {
        silent = _silent
        status = _status
    }

    constructor(_message: String, _status: HttpResponseStatus) : this(_message, _status, false)
    constructor(_status: HttpResponseStatus) : this(_status.reasonPhrase(), _status)


}
