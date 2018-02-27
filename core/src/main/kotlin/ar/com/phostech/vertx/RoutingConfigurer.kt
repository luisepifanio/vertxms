package ar.com.phostech.vertx

interface RoutingConfigurer {
    fun configureRoutesOn(mounter: Mounter)
}
