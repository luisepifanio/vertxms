package ar.com.phostech.vertx.exceptions

import ar.com.phostech.vertx.response.FailedResponse
import ar.com.phostech.vertx.response.Response
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders.CONTENT_TYPE
import io.vertx.ext.web.RoutingContext
import java.util.*
import java.util.function.Function
import java.util.function.Supplier
import kotlin.collections.ArrayList

class HttpFailureHandler(val showDetails: Boolean) : Handler<RoutingContext> {
    override fun handle(context: RoutingContext) {
        val response = context.response()
        val failure = context.failure()

        val stack = ArrayList<String>()

        if (showDetails && context.failure() != null) {
            stack.addAll(
                context.failure().stackTrace
                    .map { element -> element.toString() }
            )
        }

        val options = LinkedHashMap<Supplier<Boolean>, Function<RoutingContext, out Response>>()
        options[Supplier { !context.statusCode().equals(-1) }] = Function { ctx: RoutingContext ->
            FailedResponse(
                ctx.statusCode(),
                ctx.response().statusMessage,
                stack
            )
        }

        options[Supplier { failure is HttpFailure }] = Function { ctx: RoutingContext ->
            FailedResponse(
                (ctx.failure() as HttpFailure).status.code(),
                ctx.failure().toString().replace("[\r?\n]+", " "),
                stack
            )
        }

        options[Supplier { true }] = Function { ctx: RoutingContext ->
            FailedResponse(
                500,
                ctx.failure().toString().replace("[\r?\n]+", " "),
                stack
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
