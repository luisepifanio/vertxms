package ar.com.phostech.demo.routers

import ar.com.phostech.demo.Main
import ar.com.phostech.demo.consumers.BusPaths
import ar.com.phostech.vertx.Mountable
import ar.com.phostech.vertx.response.FailedResponse
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.util.*
import java.util.function.Supplier

class CustomerSatisfactionRouter
@Inject constructor(
    val vertx: Vertx
) : Mountable {
    override fun mount(router: Router) {
        router.get(RouterPaths.HOTJAR_LOGIN).handler(this::loginAndGetKey)
    }

    private val log = LoggerFactory.getLogger(Main::class.java)

    fun loginAndGetKey(context: RoutingContext) {
        val eb = vertx.eventBus()
        eb.send<JsonObject>(BusPaths.HOTJAR_LOGIN,
            null,
            { ar ->
                val options = LinkedHashMap<Boolean, Supplier<String>>()
                options[true] = Supplier { ar.result().body().toString() }
                options[false] = Supplier {
                    FailedResponse(
                        500,
                        ar.cause().toString().replace("[\r?\n]+", " "),
                        ar.cause().stackTrace.map { element -> element.toString() }
                    ).enconde()
                }

                val supplier = options.getOrDefault(ar.succeeded(), Supplier { "{}" })

                if (context.response().closed()) {
                    log.error("Response was closed before answer")
                } else {
                    context.response()
                        .putHeader(
                            HttpHeaderNames.CONTENT_TYPE,
                            HttpHeaderValues.APPLICATION_JSON
                        )
                        .end(supplier.get())
                }
            })
    }

}
