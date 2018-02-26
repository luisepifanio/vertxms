package ar.com.phostech.vertx.response

import java.io.Serializable

interface Response : Serializable {
    fun succeeded(): Boolean {
        return true
    }

    fun enconde(): String
}
