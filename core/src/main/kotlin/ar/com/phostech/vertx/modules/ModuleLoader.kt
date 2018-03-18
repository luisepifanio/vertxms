package ar.com.phostech.vertx.modules

import java.util.ServiceLoader

import com.google.inject.AbstractModule
import com.google.inject.Module

class ModuleLoader<M : Module>(private val type: Class<M>) : AbstractModule() {

    override fun configure() {
        val modules = ServiceLoader.load(type)
        for (module in modules) {
            install(module)
        }
    }

    companion object {
        fun <M : Module> of(type: Class<M>): ModuleLoader<M> {
            return ModuleLoader(type)
        }
    }

    //Within myjar.jar: META-INF/services/com.google.inject.Module
    //com.example.webapps.MyServletModuleA
    //com.example.webapps.MyServletModuleB
}
