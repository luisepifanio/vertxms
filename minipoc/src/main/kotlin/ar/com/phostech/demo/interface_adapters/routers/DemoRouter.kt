package ar.com.phostech.demo.interface_adapters.routers

import ar.com.phostech.vertx.Mountable
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.*


class DemoRouter : Mountable {
    override fun mount(router: Router) {
        router.get("/metric/:metric_id").handler(this::getMetric)
    }

    protected fun getMetric(context: RoutingContext) {
        // TransactionTag.put(context.request(), "get_authentication_capability")
        val metricId: String = context.pathParam("metric_id")

        val result = JsonObject()
            .put("id", metricId)
            .put("when", Date().toInstant())
        context.response().end(result.encode())
    }

}
