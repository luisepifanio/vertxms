package ar.com.phostech.demo

import ar.com.phostech.demo.modules.BasicModule
import ar.com.phostech.demo.modules.DevelopmentModule
import ar.com.phostech.demo.modules.ProductionModule
import ar.com.phostech.demo.routers.BinLookUpRouter
import ar.com.phostech.vertx.Application
import ar.com.phostech.vertx.Mounter
import ar.com.phostech.vertx.VertxApplication
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

class Main
@Inject constructor(
    val binRouter: BinLookUpRouter
) : Application {

    private val log = LoggerFactory.getLogger(Main::class.java)

    override fun configureRoutesOn(mounter: Mounter) {
        Objects.requireNonNull(mounter,"mounter should not be null")
        log.info("configuring routers")
        mounter.mount("/api", binRouter)
    }

    override fun configureConsumerOn(eventBus: EventBus) {
        Objects.requireNonNull(eventBus,"eventBus should not be null")
        log.info("configuring event consumers")
        super.configureConsumerOn(eventBus)
    }
}
