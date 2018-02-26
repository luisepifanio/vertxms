package ar.com.phostech.vertx

/**
 * @author luis
 * Date 24/02/18 19:56
 * Project: vertxms
 */
interface RoutingConfigurer {
    fun configureRoutesOn(mounter: Mounter)
}
