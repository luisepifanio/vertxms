package ar.com.phostech.demo

import ar.com.phostech.demo.modules.BasicModule
import ar.com.phostech.demo.modules.DevelopmentModule
import ar.com.phostech.demo.modules.ProductionModule
import ar.com.phostech.vertx.*
import ar.com.phostech.vertx.core.env.RuntimeEnvironment
import com.google.inject.Inject
import com.google.inject.Module
import io.vertx.core.eventbus.EventBus
import io.vertx.core.logging.LoggerFactory
import java.util.*
import java.util.function.Supplier
import kotlin.collections.ArrayList
import kotlin.collections.set

fun main(args: Array<String>) {
    val application = VertxApplication(Main::class.java, modules())
    application.start()
}

fun modules(): MutableIterable<Module>? {
    val env = RuntimeEnvironment.get()
    val modules = ArrayList<Module>()
    modules.add(BasicModule())

    // No complexity trick ;)
    val provider = LinkedHashMap<Supplier<Boolean>, Module>()
    provider[Supplier { env.development() }] = DevelopmentModule()
    provider[Supplier { true }] = ProductionModule()

    modules.add(
        provider.entries
            .first { entry -> entry.key.get() }
            .value
    )

    return modules
}

@JvmSuppressWildcards // Prevents troubles about Kotlin gerenic variance and Guice strict type checking.
class Main
@Inject constructor(
    val mountables: Set<Mountable>,
    val consumers: Set<EventBusConsumer>
) : Application {

    private val log = LoggerFactory.getLogger(Main::class.java)

    override fun configureRoutesOn(mounter: Mounter) {
        Objects.requireNonNull(mounter, "mounter should not be null")
        Objects.requireNonNull(mountables, "mountables should not be null")
        //log.info("configuring routers")
        mountables.stream().forEach({ mountable: Mountable ->
            //log.info("mounting router ${mountable::class.java.name}")
            mounter.mount("/api", mountable)
        })
    }

    override fun configureConsumerOn(eventBus: EventBus) {
        Objects.requireNonNull(eventBus, "eventBus should not be null")
        // log.info("configuring event consumers")
        consumers.stream().forEach({ mountable: EventBusConsumer ->
            //log.info("mounting consumer ${mountable::class.java.name}")
            mountable.mount(eventBus)
        })
    }
}
