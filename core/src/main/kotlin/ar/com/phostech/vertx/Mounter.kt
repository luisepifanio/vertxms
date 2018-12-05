package ar.com.phostech.vertx

interface Mounter {
    fun mount(path: String, unit: Mountable)
}
