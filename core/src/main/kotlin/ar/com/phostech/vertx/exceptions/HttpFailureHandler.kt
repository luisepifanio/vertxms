package ar.com.phostech.vertx.exceptions

import ar.com.phostech.vertx.response.Response
import ar.com.phostech.vertx.response.ResponseBuilder.fromThrowable
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import io.vertx.ext.web.RoutingContext
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

class HttpFailureHandler(val showDetails: Boolean) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext) {
        val response = context.response()
        val failure = context.failure()

        val options = LinkedHashMap<Supplier<Boolean>, Function<RoutingContext, out Response<Any>>>()
        options[Supplier { !context.statusCode().equals(-1) }] = Function { ctx: RoutingContext ->
            fromThrowable<Any>(
                ctx.statusCode(),
                ctx.response().statusMessage,
                context.failure()
            )
        }

        options[Supplier { failure is HttpFailure }] = Function { ctx: RoutingContext ->
            fromThrowable<Any>(
                (ctx.failure() as HttpFailure).status.code(),
                context.failure()
            )
        }

        options[Supplier { true }] = Function { ctx: RoutingContext ->
            fromThrowable<Any>(
                ctx.failure()
            )
        }

        val encoded: String = options.entries.stream()
            .filter { entry -> entry.key.get() }
            .findFirst()
            .map { entry -> entry.value.apply(context).enconde() }
            .orElse("")

        response.putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .end(encoded)
    }
}
