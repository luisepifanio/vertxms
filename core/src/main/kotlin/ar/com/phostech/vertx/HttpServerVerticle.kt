package ar.com.phostech.vertx

import ar.com.phostech.vertx.exceptions.HttpFailureHandlerProvider
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler


class HttpServerVerticle @Inject constructor(val configurator: RoutingConfigurer) : Mounter, AbstractVerticle() {

    internal var root: Router

    init {
        root = Router.router(vertx)
    }

    override fun mount(path: String, unit: Mountable) {
        val sub: Router = Router.router(vertx)
        unit.mount(sub)
        root.mountSubRouter(path, sub)
    }

    override fun start() {
        configureInitialRoutes()
        buildHttpServer()
            .requestHandler(root::accept)
            .listen()
    }

    private fun buildHttpServer(): HttpServer {
        val options = HttpServerOptions()
            .setCompressionSupported(true)
            .setPort(8080)
        return vertx.createHttpServer(options)
    }

    private fun configureInitialRoutes() {
        initDefaultHandlers()
        mount("/", HealthCheckRouter())
        configurator.configureRoutesOn(this)
        root.route().failureHandler(HttpFailureHandlerProvider.get())
    }

    private fun initDefaultHandlers() {
        root.route().handler(BodyHandler.create())
        root.route().handler(this::contentTypeHandler)
    }

    private fun contentTypeHandler(routingContext: RoutingContext) {
        routingContext.response().putHeader(CONTENT_TYPE, APPLICATION_JSON)
        routingContext.next()
    }

}
