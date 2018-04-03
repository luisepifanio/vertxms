package ar.com.phostech.vertx.response

import io.vertx.core.json.JsonObject
import java.util.Collections.emptyList

class FailedResponse<T>
constructor(
    val code: Int,
    val message: String,
    val stack: List<String> = listOf(),
    val errorDetails: List<ErrorDetail> = listOf()
) : Response<T> {

    constructor(
        code: Int,
        message: String
    ) : this(code, message, listOf<String>(), listOf<ErrorDetail>())


    constructor(
        code: Int,
        message: String,
        errorDetails: List<ErrorDetail>
    ) : this(code, message, listOf<String>(), errorDetails)

    override fun succeeded(): Boolean {
        return false
    }

    override fun getData(): T? {
        return null
    }

    override fun enconde(): String {
        return JsonObject()
            .put("error", JsonObject()
                .put("code", code)
                .put("details", errorDetails)
                .put("message", message)
            ).put("stack", stack)
            .encode()
    }
}
