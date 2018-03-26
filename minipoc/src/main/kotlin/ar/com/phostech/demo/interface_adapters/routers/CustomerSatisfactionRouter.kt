package ar.com.phostech.demo.interface_adapters.routers

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.demo.entities.NoInsightsCredentials
import ar.com.phostech.demo.interface_adapters.consumers.BusPaths
import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinition
import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinitionImpl
import ar.com.phostech.demo.usecases.services.insights.presenter.ShowInsightsCredentialsAsJsonPresenter
import ar.com.phostech.vertx.Mountable
import ar.com.phostech.vertx.response.FailedResponse
import ar.com.phostech.vertx.response.Response
import ar.com.phostech.vertx.response.ResponseBuilder
import com.google.inject.Inject
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.net.HttpURLConnection
import java.net.URLConnection
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

        val presenter = ShowInsightsCredentialsAsJsonPresenter(context)
        val useCase: InsigthsLoginServiceDefinition = InsigthsLoginServiceDefinitionImpl(eb)
        useCase.obtainCredentials(presenter)

        eb.send<JsonObject>(BusPaths.HOTJAR_LOGIN,
            null,
            { asyncResult ->

                if (context.response().closed()) {
                    log.error("Response was closed before answer")
                } else {
                    val options2 = LinkedHashMap<
                        Boolean,
                        Function1<AsyncResult<Message<JsonObject>>, String>
                        >()

                    options2[true] = this::result
                    options2[false] = this::fromThrowable

                    context.response()
                        .putHeader(
                            HttpHeaderNames.CONTENT_TYPE,
                            HttpHeaderValues.APPLICATION_JSON
                        )
                        .end(
                            options2.getOrDefault(
                                asyncResult.succeeded(), //
                                this::default            //
                            ).invoke(asyncResult)        //
                        )
                }
            })
    }


    private fun default(asyncResult: AsyncResult<Message<JsonObject>>): String {
        return ResponseBuilder.fromFailure<Any>(
            HttpURLConnection.HTTP_INTERNAL_ERROR,
            "Could not handle request"
        ).enconde()
    }

    private fun result(asyncResult: AsyncResult<Message<JsonObject>>): String {
        return when {
            asyncResult.result().body().containsKey("error") -> asyncResult.result().body().toString()
            asyncResult.result().body().containsKey("data") -> asyncResult.result().body().getJsonObject("data").encode()
            else -> default(asyncResult)
        }
    }

    private fun fromThrowable(asyncResult: AsyncResult<Message<JsonObject>>): String {
        return ResponseBuilder.fromThrowable<Any>(asyncResult.cause()).enconde()
    }

}
