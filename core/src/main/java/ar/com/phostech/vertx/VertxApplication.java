package ar.com.phostech.vertx;

import ar.com.phostech.vertx.core.env.ConcurrentConstants;
import ar.com.phostech.vertx.modules.VertxApplicationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.inject.util.Modules.override;


public class VertxApplication {

    private static final Logger log = LoggerFactory.getLogger(VertxApplication.class);

    private final Vertx vertx;
    private final Module modules;
    private final List<Verticle> verticles = new ArrayList<>();

    public VertxApplication(
            Class<? extends Application> applicationClass,
            Iterable<Module> applicationModules
    ) {
        vertx = buildVertx();
        modules = override(
                new VertxApplicationModule(vertx, applicationClass)
        ).with(applicationModules);
        IntStream.rangeClosed(1, httpServersCount())
                .forEach(i -> verticles.add(instantiateWithName("http-" + i, HttpServerVerticle.class)));

        verticles.add(instantiateWithName("event-consumers", EventConsumerVerticle.class));
        aditionalVerticles().forEach((name, verticleClass) -> verticles.add(instantiateWithName(name, verticleClass)));
    }

    private Vertx buildVertx() {
        final VertxOptions options = new VertxOptions();
        return Vertx.vertx(options);
    }

    public <T extends Verticle> Map<String, Class<T>> aditionalVerticles() {
        return Collections.emptyMap();
    }

    private int httpServersCount() {
        final int cpus = Runtime.getRuntime().availableProcessors();
        final int howManyServers = Math.max(cpus - 1, 1);
        log.info("With {0} CPUs we can deploys {1} http server instances ", cpus, howManyServers);
        return howManyServers;
    }

    private <T extends Verticle> T instantiateWithName(String name, Class<T> clazz) {
        final Injector injector = Guice.createInjector(modules, binder -> {
            binder.bind(VerticleIdentifier.class).toInstance(new VerticleIdentifier(name));
        });
        return injector.getInstance(clazz);
    }

    public Future<Void> start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        final List<Future> deployments = verticles.stream()
                .map(verticle -> deploy(verticle))
                .collect(Collectors.toList());
        return CompositeFuture.all(deployments)
                .setHandler(asyncResult -> {
                    if (asyncResult.succeeded()) {
                        log.info("All verticles deployed!");
                    } else {
                        log.error("Gt some troubles deploying verticles", asyncResult.cause());
                    }
                })
                .mapEmpty();
    }

    // SMELL: Too Lazy to use a match/act pattern
    private Future<String> deploy(Verticle verticle) {
        final Future<String> deployment = Future.future();

        if (verticle instanceof EventConsumerVerticle) {
            final DeploymentOptions options = new DeploymentOptions()
                    .setWorkerPoolName("mypool")
                    .setWorkerPoolSize(ConcurrentConstants.INSTANCE.getIO_POOL_SIZE())
                    .setWorker(true)
                    //.setMultiThreaded(true)
                    ;
            vertx.deployVerticle(verticle, options, deployment);
        } else {
            vertx.deployVerticle(verticle, deployment);
        }

        return deployment;
    }

    public void stop() {
        tryToCloseVertx();
        flushLogs(); // Expect no more loggers from here!
        System.out.println("This should be last what you see");
    }

    /**
     * Close vert.x instance and wait for proper undeployment
     */
    private void tryToCloseVertx() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        vertx.close(handler -> future.complete(null));
        try {
            future.get();
        } catch (final Exception e) {
            log.error("Error closing buildVertx infrastructure...", e);
        }
    }

    private void flushLogs() {
        Arrays.stream(java.util.logging.Logger.getGlobal().getHandlers())
                .forEach(handler -> {
                    handler.flush();
                    handler.close();
                });
    }
}
