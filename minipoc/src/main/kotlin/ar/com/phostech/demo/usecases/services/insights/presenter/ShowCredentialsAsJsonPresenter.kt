package ar.com.phostech.demo.usecases.services.insights.presenter

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.vertx.response.FailedResponse
import ar.com.phostech.vertx.response.Response
import ar.com.phostech.vertx.response.ResponseBuilder
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.ext.web.RoutingContext

class ShowCredentialsAsJsonPresenter
constructor(
    val context: RoutingContext
) : ShowCredentialsOutputBoundaries {
    override fun success(response: Response<InsigthsCredentials>) {
        context.response()
            .putHeader(
                HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON
            )
            .end(response.enconde())
    }

    override fun failure(throwable: Throwable?) {
        val failed: FailedResponse<InsigthsCredentials> = ResponseBuilder.fromThrowable<InsigthsCredentials>(throwable)

        context.response()
            .setStatusCode(failed.code)
            .putHeader(
                HttpHeaderNames.CONTENT_TYPE,
                HttpHeaderValues.APPLICATION_JSON
            )
            .end(failed.enconde())
    }
}
