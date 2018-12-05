package ar.com.phostech.demo.interface_adapters.routers

import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinition
import ar.com.phostech.demo.usecases.services.InsigthsLoginServiceDefinitionImpl
import ar.com.phostech.demo.usecases.services.insights.presenter.ShowCredentialsAsJsonPresenter
import ar.com.phostech.vertx.Mountable
import com.google.inject.Inject
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext

class ShowCredentialsRouter
@Inject constructor(
    val vertx: Vertx
) : Mountable {

    private val log = LoggerFactory.getLogger(ShowCredentialsRouter::class.java)

    override fun mount(router: Router) {
        router.get(RouterPaths.HOTJAR_LOGIN).handler(this::loginAndGetKey)
    }

    fun loginAndGetKey(context: RoutingContext) {
        val eb = vertx.eventBus()

        val presenter = ShowCredentialsAsJsonPresenter(context)
        val useCase: InsigthsLoginServiceDefinition = InsigthsLoginServiceDefinitionImpl(eb)
        useCase.obtainCredentials(presenter)
    }
}
