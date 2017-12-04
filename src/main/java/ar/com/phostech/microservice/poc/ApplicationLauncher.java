package ar.com.phostech.microservice.poc;

import com.intapp.vertx.guice.GuiceVertxLauncher;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.LogManager;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.intapp.vertx.guice.GuiceVertxLauncher;

import ar.com.phostech.microservice.poc.modules.BusinessModule;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

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
        log.info("Running on:" + new File(".").getAbsolutePath());
        new ApplicationLauncher().dispatch(args);
    }

    @Override
    protected Injector createInjector(Vertx vertx) {
        log.info("Creating injector");
        Injector injector = Guice.createInjector(getModules(vertx));
        return injector;
    }

    @Override
    protected List<Module> getModules(Vertx vertx) {
        log.info("Building modules");

        List<Module> modules = super.getModules(vertx);
        modules.add(new BusinessModule());
        return modules;
    }
}
