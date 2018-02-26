package ar.com.phostech.vertx

/**
 * @author luis
 * Date 24/02/18 20:00
 * Project: vertxms
 */
interface Mounter {
    fun mount(path: String, unit: Mountable)
}
