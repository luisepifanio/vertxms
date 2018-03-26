package ar.com.phostech.vertx.response

import io.vertx.core.json.JsonObject
import java.util.*

class FailedResponse<T>(
    val code: Int,
    val message: String,
    val stack: List<String> = Collections.emptyList()
) : Response<T> {
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
                .put("message", message)
            ).put("stack", stack)
            .encode()
    }
}
