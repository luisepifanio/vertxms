package ar.com.phostech.demo.modules

import ar.com.phostech.vertx.core.configuration.ConfigurationService
import ar.com.phostech.vertx.core.configuration.InMemoryConfigurationService
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import java.io.FileReader
import java.util.*


class DevelopmentModule : AbstractModule() {
    override fun configure() {}

    @Provides
    @Singleton
    fun development(): Dependency {
        return object : Dependency {
            override fun getMessage(name: String): String {
                return "${name} you're in DEV"
            }
        }
    }

    @Provides
    @Singleton
    fun configuration(): ConfigurationService {
        val applicationProperties = Properties()

        // val p = Paths.get(".environment")
        // val exists = Files.exists(p)
        // println("${p.toAbsolutePath()} exists: ${exists}")

        applicationProperties.load(FileReader(".environment"))

        val service = InMemoryConfigurationService()

        applicationProperties.forEach { key, value ->
            service.setProperty(key.toString(), value.toString(), false)
        }

        service.debugPrintSystemProperties()

        return service
    }
}
