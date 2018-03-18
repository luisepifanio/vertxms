package ar.com.phostech.demo.modules

import ar.com.phostech.demo.consumers.CustomerSatisfactionConsumer
import ar.com.phostech.demo.routers.CustomerSatisfactionRouter
import ar.com.phostech.demo.routers.DemoRouter
import ar.com.phostech.vertx.EventBusConsumer
import ar.com.phostech.vertx.Mountable
import ar.com.phostech.vertx.core.circuitbreakers.CircuitBreakerFactory
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.multibindings.Multibinder.newSetBinder
import com.google.inject.name.Named
import com.google.inject.name.Names
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import java.time.Duration.ofMinutes
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


class BasicModule : AbstractModule() {
    override fun configure() {
        /**ROUTERS**/
        // Allow to reference each Mountable with
        // @Named("demo") Mountable x
        // If needed
        bind(Mountable::class.java)
            .annotatedWith(Names.named("demo"))
            .to(DemoRouter::class.java)

        bind(Mountable::class.java)
            .annotatedWith(Names.named("customer"))
            .to(CustomerSatisfactionRouter::class.java)

        // Or reference a Set of them to use it in Application iniitalization
        val routerMultiBinder = newSetBinder(binder(), Mountable::class.java)
        routerMultiBinder.addBinding().to(DemoRouter::class.java)
        routerMultiBinder.addBinding().to(CustomerSatisfactionRouter::class.java)

        /**CONSUMERS**/
        bind(EventBusConsumer::class.java)
            .annotatedWith(Names.named("customer"))
            .to(CustomerSatisfactionConsumer::class.java)

        val consumerMultiBinder = newSetBinder(binder(), EventBusConsumer::class.java)
        consumerMultiBinder.addBinding().to(CustomerSatisfactionConsumer::class.java)

    }

    @Provides
    @Singleton
    fun service(dependency: Dependency): Service {
        return Service(dependency)
    }

    @Provides
    @Named("hotjarCircuitBreaker")
    @Singleton
    fun hotjarCircuitBreaker(circuitBreakerFactory: CircuitBreakerFactory): CircuitBreaker {
        return circuitBreakerFactory.create("kvs-storage", Consumer { options ->
            options
                .setFallbackOnFailure(true)
                .setMaxRetries(0)
                .setMaxFailures(50)
                .setTimeout(TimeUnit.SECONDS.toMillis(4))
                .setMetricsRollingWindow(ofMinutes(1).toMillis())
                .setResetTimeout(TimeUnit.SECONDS.toMillis(8))

        })
    }


    @Provides
    @Named("hotjarCircuitBreaker")
    @Singleton
    private fun hotjarWebClientOptions(): WebClientOptions {
        return WebClientOptions()
            .setDefaultHost("insights.hotjar.com")
            .setTryUseCompression(true)
            .setMaxPoolSize(64)
            .setMaxWaitQueueSize(128)
            .setDefaultPort(443)
            .setSsl(true)
            .setTrustAll(true)
    }

    @Provides
    @Singleton
    fun webClient(
        vertx: Vertx,
        @Named("hotjarCircuitBreaker") options: WebClientOptions
    ): WebClient {
        return WebClient.create(vertx, options)
    }
}
