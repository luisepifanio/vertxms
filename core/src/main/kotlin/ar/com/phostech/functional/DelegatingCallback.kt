package ar.com.phostech.functional

import java.util.*
import java.util.function.Consumer

class DelegatingCallback<T>
constructor(
    private val success: Consumer<T>,
    private val failure: Consumer<Throwable>
) : Callback<T> {

    init {
        Objects.requireNonNull(success, "success consumer should not be null")
        Objects.requireNonNull(failure, "failure consumer should not be null")
    }


    override fun success(t: T) {
        success.accept(t)
    }

    override fun failure(throwable: Throwable) {
        failure.accept(throwable)
    }
}
