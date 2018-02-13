package ar.com.phostech.vertx.core.env

import java.util.Optional.ofNullable

object ExecutionEnvironment : RuntimeEnvironment {

    private val DEV = "[DEV]"

    override fun development(): Boolean {
        return DEV == scope()
    }

    override fun application(): String {
        return ofNullable(System.getenv("APPLICATION"))
            .orElse("UNKNOWN")
    }

    override fun scope(): String {
        return ofNullable(System.getenv("SCOPE"))
            .orElse(DEV)
    }

}
