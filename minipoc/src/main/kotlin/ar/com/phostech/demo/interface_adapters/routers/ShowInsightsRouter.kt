package ar.com.phostech.demo.interface_adapters.routers

import ar.com.phostech.vertx.Mountable
import com.google.inject.Inject
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ShowInsightsRouter
@Inject constructor(
    val vertx: Vertx
) : Mountable {

    private val log = LoggerFactory.getLogger(ShowInsightsRouter::class.java)

    override fun mount(router: Router) {
        router.get("/hotjar/feedbacks/from/:start/to/:end/on/:country").handler(this::fetchInsights)
    }

    fun fetchInsights(context: RoutingContext) {

    }
}
