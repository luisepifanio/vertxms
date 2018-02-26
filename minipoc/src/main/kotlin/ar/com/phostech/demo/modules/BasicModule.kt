package ar.com.phostech.demo.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton


class BasicModule : AbstractModule() {
    override fun configure() { }

    @Provides
    @Singleton
    fun service(dependency: Dependency): Service {
        return Service(dependency)
    }
}
