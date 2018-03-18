package ar.com.phostech.vertx.core.circuitbreakers

import io.vertx.circuitbreaker.CircuitBreakerOptions
import java.util.concurrent.TimeUnit.MINUTES

class DefaultCircuitBreakerOptions : CircuitBreakerOptions() {
    init {
        this.metricsRollingWindow = MINUTES.toMillis(1)
    }
}
