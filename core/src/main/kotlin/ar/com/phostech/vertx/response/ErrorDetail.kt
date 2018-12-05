package ar.com.phostech.vertx.response

import java.io.Serializable

data class ErrorDetail(
    val code: String,
    val message: String,
    var field: String? = null,
    var metadata: Map<String, String>? = null
) : Serializable
