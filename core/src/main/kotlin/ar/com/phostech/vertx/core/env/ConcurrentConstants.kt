package ar.com.phostech.vertx.core.env

import java.math.BigDecimal


object ConcurrentConstants {

    val IO_BLOCKING_COEFFICIENT = BigDecimal("0.9")
    val COMPUTATION_BLOCKING_COEFFICIENT = BigDecimal("0.1")
    val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

    val IO_POOL_SIZE = poolSize(IO_BLOCKING_COEFFICIENT)
    val COMPUTATION_POOL_SIZE = poolSize(COMPUTATION_BLOCKING_COEFFICIENT)


    private fun poolSize(blockingCoefficient: BigDecimal): Int {
        // Anti ass hole validation
        assert(blockingCoefficient < BigDecimal.ONE)
        assert(blockingCoefficient > BigDecimal.ZERO)

        return BigDecimal(NUMBER_OF_CORES)
                .div(BigDecimal.ONE.minus(blockingCoefficient))
            .toInt()

    }

}
