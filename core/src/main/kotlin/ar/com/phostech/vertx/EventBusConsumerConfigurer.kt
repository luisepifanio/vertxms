package ar.com.phostech.vertx

import io.vertx.core.eventbus.EventBus

/**
 * @author luis
 * Date 24/02/18 19:56
 * Project: vertxms
 */
interface EventBusConsumerConfigurer {

    fun configureConsumerOn(eventBus: EventBus) {}

}
