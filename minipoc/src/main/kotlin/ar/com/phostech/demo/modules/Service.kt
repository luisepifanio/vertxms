package ar.com.phostech.demo.modules

import com.google.inject.Inject

/**
 * @author luis
 * Date 25/02/18 17:42
 * Project: vertxms
 */
class Service @Inject constructor(val dependency: Dependency) {

    fun call(name: String): String {
        return "Service responded: ${dependency.getMessage(name)}"
    }

}
