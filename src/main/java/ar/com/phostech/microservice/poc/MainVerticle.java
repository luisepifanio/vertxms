package ar.com.phostech.microservice.poc;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import ar.com.phostech.microservice.poc.modules.Dependency;
import ar.com.phostech.microservice.poc.verticles.GreeterVerticle;
import ar.com.phostech.microservice.poc.verticles.ServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Implements verticle to show dependency injection in action.
 * The following dependencies are injected:
 * 1) Vertx instance which bindings are configured in {@link com.intapp.vertx.guice.VertxModule}
 * 2) Application specific dependency
 */
public class MainVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    private final Dependency dependency;
    private final Vertx vertx;

    @Inject
    public MainVerticle(final Vertx vertx, final Dependency dependency) {
        log.info("Instantiating MainVerticle");

        this.dependency = Preconditions.checkNotNull(dependency);
        this.vertx = Preconditions.checkNotNull(vertx);
    }

    /**
    * Start method uses CompositeFuture to deploy all required verticles
    *
    * @param done
    */
    @Override
    public void start(Future<Void> done) {
        DeploymentOptions serverOpts = new DeploymentOptions()
                .setWorkerPoolSize(Runtime.getRuntime().availableProcessors());

        // TODO: Scan for verticles and then add all found
        // TODO: Find a way to describe a complete verticle deployment
        CompositeFuture.all(
              deploy(vertx, ServerVerticle.class, serverOpts)
             , deploy(vertx, GreeterVerticle.class, serverOpts)
        ).setHandler(r -> {
            if (r.succeeded()) {
                done.complete();
            } else {
                done.fail(r.cause());
            }
        });
    }

    /**
     * Fast implementation to check if @Inject is present
     *      */
    private static boolean isAnnotationPresent(Class<?> target, Class<? extends Annotation> annotation) {
        Preconditions.checkNotNull(annotation, "target annotations");

        final Predicate<AccessibleObject> predicate = m -> m.isAnnotationPresent(annotation);

        return target.isAnnotationPresent(annotation)
                || Stream.of(target.getConstructors()).anyMatch(predicate)
                || Stream.of(target.getDeclaredMethods()).anyMatch(predicate)
                || Stream.of(target.getFields()).anyMatch(predicate);
    }

    private static final String asDeploymentDescriptor(final Class<?> verticle){
        String deploymentName = verticle.getName();
        if (isAnnotationPresent(verticle, Inject.class)) {
            deploymentName = "java-guice:" + deploymentName;
        }
        log.info("Verticle: "+verticle.getCanonicalName()+" deployed as '"+ deploymentName +"'");
        return deploymentName;
    }

    /**
     * Deploy a vertx-guice verticle on a vertx instance with deployment options
     * @param vertx - Vertx instance to deploy on
     * @param verticle - Verticle class to deploy
     * @param opts - Deployment options to use for deployment
     * @return - Future that can be used to handle successful / failed deployments
     */
    private static <T extends Verticle> Future<Void> deploy(
            final Vertx vertx,
            final Class<T> verticle,
            final DeploymentOptions opts) {
        Future<Void> done = Future.future();
        String deploymentName = asDeploymentDescriptor(verticle);

        //
        JsonObject config = new JsonObject();
        opts.setConfig(config);

        vertx.deployVerticle(deploymentName, opts, r -> {
            if (r.succeeded()) {
                System.out.println("Successfully deployed verticle: " +deploymentName);
                done.complete();
            } else {
                System.out.println("Failed to deploy verticle: " + deploymentName);
                done.fail(r.cause());
            }
        });

        return done;
    }

}
