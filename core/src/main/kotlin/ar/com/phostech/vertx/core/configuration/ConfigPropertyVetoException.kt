package ar.com.phostech.vertx.core.configuration

import java.beans.*

/**
 * A PropertyVetoException is thrown when a proposed change to a
 * property represents an unacceptable value.
 *
 * @author  Emil Ivov
 *          Luis Epifanio (ported to kotlin)
 */
class ConfigPropertyVetoException
(
    message: String,
    val propertyChangeEvent: PropertyChangeEvent
) : RuntimeException(message)
