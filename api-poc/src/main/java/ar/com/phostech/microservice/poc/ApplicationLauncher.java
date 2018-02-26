package ar.com.phostech.microservice.poc;

import ar.com.phostech.microservice.poc.modules.BusinessModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.intapp.vertx.guice.GuiceVertxLauncher;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Examples which shows the following:
 * 1) Usage of the Vertx Launcher and Gradle configuration
 * 2) Extending GuiceVertxLauncher to register application specific modules
 */
public class ApplicationLauncher extends GuiceVertxLauncher {

    private static final Logger log = LoggerFactory.getLogger(ApplicationLauncher.class);

    /**
     * Main entry point.
     *
     * @param args the user command line arguments. For supported command line arguments please see {@link Launcher}.
     */
    public static void main(String[] args) {
        log.info("Running on: {0}", new File(".").getAbsolutePath());
        new ApplicationLauncher().dispatch(args);
    }

    @Override
    protected Injector createInjector(Vertx vertx) {
        // Just for knowing we can use custom Injectors
        // Injector injector = Guice.createInjector(getModules(vertx));
        // return injector;
        log.info("Creating injector");
        return super.createInjector(vertx);
    }

    @Override
    protected List<Module> getModules(Vertx vertx) {
        log.info("Building modules");

        List<Module> modules = super.getModules(vertx);
        modules.add(new BusinessModule());
        return modules;
    }
}
