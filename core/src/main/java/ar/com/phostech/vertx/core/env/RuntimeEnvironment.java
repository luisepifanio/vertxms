package ar.com.phostech.vertx.core.env;

/**
 * @author luis
 * Date 23/02/18 22:14
 * Project: vertxms
 */
public interface RuntimeEnvironment {

    /**
     * This implementation returns a default implementation for a
     * RuntimeEnvironment
     *
     * @return RuntimeEnvironment instance
     */
    static RuntimeEnvironment get() {
        return ExecutionEnvironment.INSTANCE;
    }

    /**
     * Returns true if the environment is local (dev)
     *
     * @return true if the running environment is dev
     */
    boolean development();

    /**
     * Returns the application name
     *
     * @return
     */
    String application();

    /**
     * Returns application scope
     *
     * @return
     */
    String scope();

}
