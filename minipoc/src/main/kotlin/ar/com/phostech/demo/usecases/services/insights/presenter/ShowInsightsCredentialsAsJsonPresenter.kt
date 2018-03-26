package ar.com.phostech.demo.usecases.services.insights.presenter

import ar.com.phostech.demo.entities.InsigthsCredentials
import ar.com.phostech.vertx.response.Response
import io.vertx.ext.web.RoutingContext

class ShowInsightsCredentialsAsJsonPresenter
constructor(
    val ctx: RoutingContext
) : ShowInsightsCredentialsOutputBoundaries {
    override fun success(t: Response<InsigthsCredentials>?) {
    }

    override fun failure(throwable: Throwable?) {
    }
}
