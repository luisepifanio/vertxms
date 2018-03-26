package ar.com.phostech.demo.interface_adapters.consumers

import ar.com.phostech.demo.configuration.ConfigurationKeys.HOTJAR_EMAIL
import ar.com.phostech.demo.configuration.ConfigurationKeys.HOTJAR_PASS
import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.demo.entities.NoInsightsCredentials
import ar.com.phostech.vertx.EventBusConsumer
import ar.com.phostech.vertx.core.configuration.ConfigurationService
import ar.com.phostech.vertx.response.Response
import ar.com.phostech.vertx.response.ResponseBuilder
import com.google.inject.Inject
import com.google.inject.name.Named
import io.vertx.circuitbreaker.CircuitBreaker
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
            log.info("Response from cache: ${credentials.encode()}")
            respondWith(message, Supplier {
                val stored = credentials.mapTo(InsigthsCredentials::class.java)
                ResponseBuilder.from(stored)
            })
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
                val options = LinkedHashMap<Boolean, Supplier<Response<InsigthsCredentials>>>()
                options[true] = Supplier {
                    val entity = ar.result().body().toJsonObject()
                    fromResult(entity)
                }
                options[false] = Supplier {
                    fromThrowable(ar.cause())
                }

                val supplier = options.getOrDefault(ar.succeeded(), Supplier { ResponseBuilder.from(NoInsightsCredentials) })
                respondWith(message, supplier)
            })


    }

    fun fromThrowable(th: Throwable): Response<InsigthsCredentials> {
        return ResponseBuilder.fromThrowable(th)
    }

    fun fromResult(response: JsonObject): Response<InsigthsCredentials> {
        log.info("received: {0}", response.toString())

        val credentials = Optional
            .ofNullable(response.getBoolean("success"))
            .map {
                if (it) InsigthsCredentials(
                    response.getLong("user_id"),
                    response.getString("user_name"),
                    response.getString("user_email"),
                    response.getString("access_key"),
                    true
                ) else NoInsightsCredentials
            }.get()

        configurationService.setProperty(CREDENTIALS_KEY, JsonObject.mapFrom(credentials))

        return ResponseBuilder.from(credentials)
    }

    fun respondWith(message: Message<JsonObject>, supplier: Supplier<out Response<InsigthsCredentials>>) {
        message.reply(
            JsonObject.mapFrom( // It is easier to store as a JsonObject since it is not a cyclic structure
                supplier.get()
            )
        )
    }

}
