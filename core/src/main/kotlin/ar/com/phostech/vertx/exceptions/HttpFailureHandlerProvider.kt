package ar.com.phostech.vertx.exceptions

import ar.com.phostech.vertx.core.env.RuntimeEnvironment

/**
 * @author luis
 * Date 25/02/18 19:46
 * Project: vertxms
 */
object HttpFailureHandlerProvider {
    fun get(showDetails: Boolean = RuntimeEnvironment.get().development()): HttpFailureHandler {
        return HttpFailureHandler(showDetails)
    }
}
