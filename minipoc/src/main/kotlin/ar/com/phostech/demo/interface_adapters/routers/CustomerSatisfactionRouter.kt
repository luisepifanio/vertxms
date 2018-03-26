package ar.com.phostech.demo.interface_adapters.routers

import ar.com.phostech.demo.interface_adapters.consumers.BusPaths
import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinition
import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinitionImpl
import ar.com.phostech.demo.usecases.services.insights.presenter.ShowInsightsCredentialsAsJsonPresenter
import ar.com.phostech.vertx.Mountable
import ar.com.phostech.vertx.response.FailedResponse
import ar.com.phostech.vertx.response.ResponseBuilder
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

    private val log = LoggerFactory.getLogger(CustomerSatisfactionRouter::class.java)

    fun loginAndGetKey(context: RoutingContext) {
        val eb = vertx.eventBus()
        //

        val presenter = ShowInsightsCredentialsAsJsonPresenter(context)
        val useCase: InsigthsLoginServiceDefinition = InsigthsLoginServiceDefinitionImpl(eb)
        useCase.obtainCredentials(presenter)


        ///OLD CODE

        eb.send<JsonObject>(BusPaths.HOTJAR_LOGIN,
            null,
            { asyncResult ->
                val options = LinkedHashMap<Boolean, Supplier<String>>()
                options[true] = Supplier {
                    asyncResult.result().body().toString() }
                options[false] = Supplier {
                    ResponseBuilder.fromThrowable<String>(asyncResult.cause())
                        .enconde()
                }

                val supplier = options.getOrDefault(asyncResult.succeeded(), Supplier { "{}" })

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
