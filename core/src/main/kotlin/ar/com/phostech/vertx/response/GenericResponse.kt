package ar.com.phostech.vertx.response

import io.vertx.core.json.JsonObject
import java.io.Serializable

class GenericResponse<out T : Serializable>(val data: T) : Response {
    override fun enconde(): String {
        return JsonObject.mapFrom(this.data).encode()
    }
}
