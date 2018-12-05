package ar.com.phostech.vertx.response

import java.io.Serializable

interface Response<out T> : Serializable {
    fun succeeded(): Boolean
    fun enconde(): String
    fun getData(): T?
}
