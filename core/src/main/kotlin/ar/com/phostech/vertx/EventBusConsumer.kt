package ar.com.phostech.vertx

import io.vertx.core.eventbus.EventBus

interface EventBusConsumer {
    fun mount(eventBus: EventBus)
}
