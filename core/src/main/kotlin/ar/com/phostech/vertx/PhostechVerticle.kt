package ar.com.phostech.vertx

import ar.com.phostech.functional.Either
import ar.com.phostech.functional.utils.ThrowingSupplier
import ar.com.phostech.vertx.core.env.RuntimeEnvironment
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.function.Supplier

const val DEFAULT_MODE = "prod"
const val CONFIG_PREFIX = "confprefix"
const val DEFAULT_CONFIG_PREFIX = ""

class PhostechVerticle : AbstractVerticle() {

    private val log = LoggerFactory.getLogger(PhostechVerticle::class.java)

    private var _envConfig: JsonObject? = null

    val environmentConfiguration: JsonObject
        get() = Optional.ofNullable(_envConfig).orElseGet(this::loadconfig)

    private fun loadconfig(): JsonObject {
        val env = RuntimeEnvironment.get()

        val options = LinkedHashMap<Supplier<Boolean>, String>()
        options[Supplier { env.development() }] = "dev"
        options[Supplier { env.scope().toUpperCase().startsWith("TEST") }] = "test"
        options[Supplier { true }] = DEFAULT_MODE

        val environment = options.entries
            .stream()
            .filter { entry -> entry.key.get() }
            .findFirst()
            .flatMap<String> { entry -> Optional.ofNullable(entry.value) }
            .orElse(DEFAULT_MODE)

        val prefix = System.getProperty(CONFIG_PREFIX, DEFAULT_CONFIG_PREFIX)
        val configPath = String.format(prefix + File.separator + "conf/%s/app.config.json", environment)
        val path = Paths.get(configPath)
        log.info("Read custom configuration from: " + path.toFile().absolutePath)
        //Reading a file over and over is contra performant :(
        val readFileEither = Either.fromCatching(
            ThrowingSupplier { String(Files.readAllBytes(path), StandardCharsets.UTF_8) },
            IOException::class.java
        )

        val value: JsonObject = readFileEither.fold(
            { s -> JsonObject(s) },
            { _ -> JsonObject("{}") }
        )
        _envConfig = value

        return value
    }

    override fun config(): JsonObject {
        val configuration = context.config()
        return configuration.mergeIn(environmentConfiguration, true)
    }
}
