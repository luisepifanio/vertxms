package ar.com.phostech.vertx.utils

import java.util.*

/**
 * Converts an object to a given class if possible.
 *
 * @param o     Object to attempt to cast
 * @param clazz Target class of the cast
 * @param <T>   Type of the casted object
 * @return An optional that containsa casted instance of related type, null otherwies
</T> */
fun <T> tryToCast(o: Any, clazz: Class<T>): Optional<T> {
    return if (clazz.isInstance(o)) Optional.of(clazz.cast(o)) else Optional.empty()
}
