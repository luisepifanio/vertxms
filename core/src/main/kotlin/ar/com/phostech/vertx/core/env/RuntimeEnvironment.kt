package ar.com.phostech.vertx.core.env

interface RuntimeEnvironment {

    /**
     * Returns true if the environment is development
     *
     * @return true if the running environment is dev
     */
    fun development(): Boolean

    /**
     * Returns the application name
     *
     * @return
     */
    fun application(): String

    /**
     * Returns application scope
     *
     * @return
     */
    fun scope(): String
}
