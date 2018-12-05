package ar.com.phostech.vertx.response

import io.vertx.core.json.JsonObject
import java.io.Serializable

class GenericResponse<out T>(private val data: T) : Response<T> {
    override fun getData(): T {
        return data
    }

    override fun succeeded(): Boolean {
        return true
    }

    override fun enconde(): String {
        return JsonObject.mapFrom(this.data).encode()
    }
}
