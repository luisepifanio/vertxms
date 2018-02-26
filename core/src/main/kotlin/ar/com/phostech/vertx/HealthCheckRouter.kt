package ar.com.phostech.vertx

import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class HealthCheckRouter : Mountable {
    override fun mount(router: Router) {
        router.get("/ping").handler(this::onPingHandler)
    }

    private fun onPingHandler(ctx: RoutingContext) {
        ctx.response()
            .putHeader(CONTENT_TYPE, TEXT_PLAIN)
            .end("pong")
    }
}
