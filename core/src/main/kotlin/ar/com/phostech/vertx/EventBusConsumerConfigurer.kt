package ar.com.phostech.vertx

import io.vertx.core.eventbus.EventBus

interface EventBusConsumerConfigurer {

    fun configureConsumerOn(eventBus: EventBus) {/*NO OPERATION AS DEFAULT*/}

}
