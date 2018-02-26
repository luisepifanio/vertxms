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
import java.util.LinkedHashMap
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

    override fun configureRoutesOn(mounter: Mounter) {
        mounter.mount("/api", binRouter)
    }
}
