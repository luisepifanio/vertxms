package ar.com.phostech.vertx.modules

import ar.com.phostech.vertx.Application
import ar.com.phostech.vertx.EventBusConsumerConfigurer
import ar.com.phostech.vertx.RoutingConfigurer
import com.google.inject.AbstractModule
import io.vertx.core.Vertx

class VertxApplicationModule(
    val vertx: Vertx,
    val  applicationClass : Class<out Application>
    ) : AbstractModule() {

    override fun configure() {
        bind(Application::class.java).to(applicationClass)
        bind(RoutingConfigurer::class.java).to(applicationClass)
        bind(EventBusConsumerConfigurer::class.java).to(applicationClass)
        bind(Vertx::class.java).toInstance(vertx)
        //bind(MetricCollector::class.java).toInstance(metricCollector)
    }
}
