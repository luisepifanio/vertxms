package ar.com.phostech.demo.routers

import ar.com.phostech.vertx.Mountable
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.time.ZoneId
import java.util.*


class BinLookUpRouter : Mountable {
    override fun mount(router: Router) {
        router.get("/metric/:metric_id").handler(this::getMetric)
    }

    private fun getMetric(context: RoutingContext) {
        // TransactionTag.put(context.request(), "get_authentication_capability")
        val metricId: String = context.pathParam("metric_id")

        val result = JsonObject()
            .put("id", metricId)
            .put("when", Date().toInstant())
        context.response().end(result.encode())
    }

}
