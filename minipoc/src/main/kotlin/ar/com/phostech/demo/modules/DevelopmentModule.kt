package ar.com.phostech.demo.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

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
}
