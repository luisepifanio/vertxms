package ar.com.phostech.vertx.core.circuitbreakers

import com.google.inject.Inject
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.Vertx
import io.vertx.circuitbreaker.CircuitBreakerOptions
import java.util.function.Consumer


class CircuitBreakerFactory
@Inject constructor(
    val vertx: Vertx,
    val defaultOptions: DefaultCircuitBreakerOptions

) {

    fun create(name: String, config: Consumer<CircuitBreakerOptions>): CircuitBreaker {
        val identifier = "/circuitbreaker/${name}"
        val options = CircuitBreakerOptions(defaultOptions)
        options.metricsRollingWindow = defaultOptions.metricsRollingWindow
        config.accept(options)
        return CircuitBreaker.create(identifier, vertx, options)
    }

}
