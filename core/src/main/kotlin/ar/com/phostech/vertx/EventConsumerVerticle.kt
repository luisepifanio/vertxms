package ar.com.phostech.vertx

import com.google.inject.Inject
import io.vertx.core.AbstractVerticle

class EventConsumerVerticle
@Inject constructor(val configurator: EventBusConsumerConfigurer)
    : AbstractVerticle() {

    override fun start() {
        configurator.configureConsumerOn(vertx.eventBus())
    }
}
