package ar.com.phostech.vertx

class VerticleIdentifier(val id: String = "local_default") {

    override fun toString(): String {
        return id
    }
}
