package ar.com.phostech.demo.consumers

import ar.com.phostech.demo.configuration.ConfigurationKeys.HOTJAR_EMAIL
import ar.com.phostech.demo.configuration.ConfigurationKeys.HOTJAR_PASS
import ar.com.phostech.vertx.EventBusConsumer
import ar.com.phostech.vertx.core.configuration.ConfigurationService
import ar.com.phostech.vertx.response.FailedResponse
import com.google.inject.Inject
import com.google.inject.name.Named
import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import java.util.*
import java.util.function.Supplier


class CustomerSatisfactionConsumer
@Inject constructor(
    @Named("hotjarCircuitBreaker") val circuitBreaker: CircuitBreaker,
    val webClient: WebClient,
    val configurationService: ConfigurationService
) : EventBusConsumer {

    private val log = LoggerFactory.getLogger(EventBusConsumer::class.java)
    private val CREDENTIALS_KEY = "credentials"
    private val KEYS_TO_STORE = Collections.unmodifiableList(
        listOf("access_key", "user_id", "user_email", "user_name")
    )

    override fun mount(eventBus: EventBus) {
        eventBus.consumer<JsonObject>(BusPaths.HOTJAR_LOGIN, this::handleMessage)
        log.info("Mount Finished")
    }

    private fun handleMessage(message: Message<JsonObject>) {

        log.info("Received data request")

        //Try to fetch from local storage
        val credentials = configurationService.getJsonObject(CREDENTIALS_KEY, JsonObject())

        val available = Optional.ofNullable(
            credentials.getBoolean("valid")
        ).orElse(false)

        if (available) {
            log.info("Response from cache")
            respondWith(message, Supplier { credentials })
            return
        }

        log.info("Trying to request from network")

        webClient
            .post(/*443, "insights.hotjar.com", */"/api/v1/users")
            .sendJsonObject(JsonObject()
                .put("action", "login")
                .put("email", configurationService.getString(HOTJAR_EMAIL))
                .put("password", configurationService.getString(HOTJAR_PASS))
                , { ar ->
                val options = LinkedHashMap<Boolean, Supplier<JsonObject>>()
                options[true] = Supplier {
                    val entity = ar.result().body().toJsonObject()
                    fromResult(entity)
                }
                options[false] = Supplier {
                    fromThrowable(ar.cause())
                }

                val supplier = options.getOrDefault(ar.succeeded(), Supplier { JsonObject() })
                respondWith(message, supplier)
            })


    }

    fun fromThrowable(th: Throwable): JsonObject {
        return JsonObject(
            FailedResponse(
                500,
                th.toString().replace("[\r?\n]+", " "),
                th.stackTrace.map { element -> element.toString() }
            ).enconde()
        )
    }

    fun fromResult(response: JsonObject): JsonObject {
        log.info("received: {0}", response.toString())

        val jsonObject = JsonObject()
        KEYS_TO_STORE
            .forEach { key: String ->
                jsonObject.put(key, response.getValue(key))
            }

        jsonObject.put("valid", true)
        configurationService.setProperty(CREDENTIALS_KEY, jsonObject)

        return jsonObject
    }

    fun respondWith(message: Message<JsonObject>, supplier: Supplier<JsonObject>) {
        message.reply(supplier.get())
    }

}
