package ar.com.phostech.demo.usecases.services

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.demo.entities.NoInsightsCredentials
import ar.com.phostech.demo.interface_adapters.consumers.BusPaths
import ar.com.phostech.functional.Callback
import ar.com.phostech.vertx.response.Response
import ar.com.phostech.vertx.response.ResponseBuilder
import io.vertx.core.AsyncResult
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.Message
import io.vertx.core.logging.LoggerFactory

class InsigthsLoginServiceDefinitionImpl(val eb: EventBus) : InsigthsLoginServiceDefinition {

    private val log = LoggerFactory.getLogger(InsigthsLoginServiceDefinitionImpl::class.java)

    override fun obtainCredentials(callback: Callback<Response<InsigthsCredentials>>) {
        eb.send<InsigthsCredentials>(BusPaths.HOTJAR_LOGIN,
            null,
            { asyncResult ->

                val options = LinkedHashMap<Boolean, Function1<AsyncResult<Message<InsigthsCredentials>>, Response<InsigthsCredentials>>>()
                options[true] = this::result
                options[false] = this::fromThrowable

                options.getOrDefault(asyncResult.succeeded(), this::default)
                    .invoke(asyncResult)
            })
    }

    private fun default(asyncResult: AsyncResult<Message<InsigthsCredentials>>): Response<InsigthsCredentials> {
        return ResponseBuilder.from(NoInsightsCredentials)
    }

    private fun result(asyncResult: AsyncResult<Message<InsigthsCredentials>>): Response<InsigthsCredentials> {
        return ResponseBuilder.from(asyncResult.result().body())
    }

    private fun fromThrowable(asyncResult: AsyncResult<Message<InsigthsCredentials>>): Response<InsigthsCredentials> {
        return ResponseBuilder.fromThrowable<InsigthsCredentials>(asyncResult.cause())
    }
}
