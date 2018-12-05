package ar.com.phostech.vertx

import io.vertx.ext.web.Router

/**
 * @author luis
 * Date 24/02/18 20:01
 * Project: vertxms
 */
interface Mountable {
    /**
     * Register the methods provided by this class using the given router.
     *
     *
     * The router is expected to be a relative mount path unless explicitly documented.
     *
     * @param router Route where to hook event handlers
     */
    fun mount(router: Router)
}
