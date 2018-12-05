package ar.com.phostech.demo.interface_adapters.consumers

import ar.com.phostech.utils.DateUtils
import ar.com.phostech.vertx.EventBusConsumer
import ar.com.phostech.vertx.response.ErrorDetail
import ar.com.phostech.vertx.response.ResponseBuilder
import com.google.inject.Inject
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.json.JsonObject.mapFrom
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.codec.BodyCodec
import java.net.HttpURLConnection
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.function.Supplier

class ConsumerFeedBackConsumer
@Inject constructor(
    val webClient: WebClient
) : EventBusConsumer {

    private val requiredParams = listOf(
        "site",
        "dashboard",
        "dateStart",
        "dateEnd",
        "accessKey"
    )

    private val shortFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override fun mount(eventBus: EventBus) {
        eventBus.consumer<JsonObject>(BusPaths.HOTJAR_LOGIN, this::handleMessage)
    }

    private fun handleMessage(message: Message<JsonObject>) {
        assert(
            requiredParams.all { message.headers().contains(it) },
            {
                requiredParams.filter { message.headers().contains(it) }
                    .joinToString(
                        separator = ",",
                        prefix = "These params were not found: (",
                        postfix = ")"
                    )
            }
        )

        val (
            site,
            dashboard,
            dateStart,
            dateEnd,
            accessKey
        ) = requiredParams
            .map { key: String ->
                when (key) {
                    "date" -> shortFormatter.format(DateUtils.parseToInstant(message.headers().get(key)))
                    else -> message.headers().get(key)
                }
            }

        val offset = Optional.ofNullable(message.headers().get("offset"))
            .map { it.toIntOrNull() ?: 0 }
            .orElse(0)

        webClient
            .get("/api/v1/sites/$site/feedback/$dashboard/responses")
            // query params
            .addQueryParam("sort", "-created")
            .addQueryParam("amount", 100.toString())
            .addQueryParam("offset", offset.toString())
            .addQueryParam("count", "true")
            .addQueryParam("fields", "browser,content,country_code,created_datetime_string,device,id,index,os,response_url,window_size")
            .addQueryParam("filter", "created__ge__${dateStart},created__le__${dateEnd}")
            // headers
            .putHeader("x-access-key", accessKey)
            .putHeader("content-type", "application/json")
            .putHeader("accept", "application/application/json")
            //
            .`as`(BodyCodec.jsonObject())
            .send { asyncResult: AsyncResult<HttpResponse<JsonObject>> ->
                val options = LinkedHashMap<
                    Boolean,
                    Function1<AsyncResult<HttpResponse<JsonObject>>, JsonObject>
                    >()

                options[true] = this::result
                options[false] = this::fromThrowable

                this.respondWith(message, Supplier {
                    options.getOrDefault(
                        asyncResult.succeeded(), // Always called
                        this::default            // Never called, but prefer JVM optimizations a this point
                    ).invoke(asyncResult)

                })
            }
    }

    fun default(asyncResult: AsyncResult<HttpResponse<JsonObject>>): JsonObject {
        asyncResult // not used, ignore compiler warn
        return mapFrom(
            ResponseBuilder.fromFailure<Any>(
                HttpURLConnection.HTTP_INTERNAL_ERROR,
                "Could not handle request"
            )
        )
    }

    fun result(asyncResult: AsyncResult<HttpResponse<JsonObject>>): JsonObject {
        return when {
            checkErrorResponse(asyncResult) -> mapFrom(
                ResponseBuilder.fromFailure<Any>(
                    HttpURLConnection.HTTP_UNAVAILABLE,
                    "Underlying services not available",
                    listOf(
                        ErrorDetail(
                            "dependency.failed",
                            "Data source invocation failed"
                        )
                    )
                )
            )
            asyncResult.result().body().containsKey("data") -> asyncResult.result().body()

            else -> default(asyncResult)
        }
    }

    private fun checkErrorResponse(asyncResult: AsyncResult<HttpResponse<JsonObject>>) =
        asyncResult.result().body().containsKey("error") ||
            !asyncResult.result().body().getBoolean("success")

    fun fromThrowable(asyncResult: AsyncResult<HttpResponse<JsonObject>>): JsonObject {
        return mapFrom(
            ResponseBuilder.fromThrowable<Any>(asyncResult.cause())
        )
    }

    fun respondWith(message: Message<JsonObject>, supplier: Supplier<JsonObject>) {
        message.reply(
            mapFrom( // It is easier to store as a JsonObject since it is not a cyclic structure
                supplier.get()
            )
        )
    }
}
