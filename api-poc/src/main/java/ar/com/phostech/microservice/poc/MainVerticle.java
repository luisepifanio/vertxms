package ar.com.phostech.microservice.poc;

import ar.com.phostech.functional.Either;
import ar.com.phostech.microservice.poc.modules.Dependency;
import ar.com.phostech.vertx.core.env.ExecutionEnvironment;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.intapp.vertx.guice.GuiceVerticleFactory;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

/**
 * Implements verticle to show dependency injection in action.
 * The following dependencies are injected:
 * 1) Vertx instance which bindings are configured in {@link com.intapp.vertx.guice.VertxModule}
 * 2) Application specific dependency
 */
public class MainVerticle extends AbstractVerticle {

    //Constants
    private static final String ENVIRONMENT_MODE = "env";
    private static final String DEFAULT_MODE = "prod";
    private static final String CONFIG_PREFIX ="confprefix";
    private static final String DEFAULT_CONFIG_PREFIX ="";

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

        log.info("Main verticle configuration:\n" + this.config().toString());
        log.info("processArgs:\n" + processArgs());

        // Create a new JavaClassLoader
        ClassLoader classLoader = this.getClass().getClassLoader();

        Set<Either<Class<?>,ClassNotFoundException>> classesTried = config().getJsonObject("verticles")
                .fieldNames()
                .stream()
                .map(className -> {
                    //log.info(format("Trying to load class {0} ...",className));
                    Either<Class<?>,ClassNotFoundException> classToLoad = Either.fromCatching(
                            () -> classLoader.loadClass(className),
                            ClassNotFoundException.class
                    );
                    return classToLoad;
                })
                .collect(Collectors.toSet())
                ;

        classesTried.stream()
                .filter(either -> either.hasAlternative())
                .forEach(failed -> System.out.println(failed.alternative().getMessage()));

        List<Future> futureDeployments = classesTried.stream()
                .filter(either -> either.hasExpected())
                // TODO: Filter loaded classes according to implementation of 'Verticle'
                //.filter(either -> either.expected().isAssignableFrom(Verticle.class))
                .map(either -> {
                    JsonObject doptsAsJson = config()
                            .getJsonObject("verticles")
                            .getJsonObject(either.expected().getName());
                    DeploymentOptions dopts = new DeploymentOptions(doptsAsJson);
                    return deploy(vertx, (Class<? extends Verticle>) either.expected(), dopts);
                })
                .collect(Collectors.toList());

        CompositeFuture.all(futureDeployments)
                .setHandler(r -> {
                    if (r.succeeded()) {
                        done.complete();
                    } else {
                        done.fail(r.cause());
                    }
                });
    }

    /**
     * Fast implementation to check if @Inject is present
     */
    private static boolean isAnnotationPresent(Class<?> target, Class<? extends Annotation> annotation) {
        Preconditions.checkNotNull(annotation, "target annotations");

        final Predicate<AccessibleObject> predicate = m -> m.isAnnotationPresent(annotation);

        return target.isAnnotationPresent(annotation)
                || Stream.of(target.getConstructors()).anyMatch(predicate)
                || Stream.of(target.getDeclaredMethods()).anyMatch(predicate)
                || Stream.of(target.getFields()).anyMatch(predicate);
    }

    private static final String asDeploymentDescriptor(final Class<?> verticle) {
        String deploymentName = verticle.getName();
        if (isAnnotationPresent(verticle, Inject.class)) {
            deploymentName = GuiceVerticleFactory.PREFIX + ":" + deploymentName;
        }
        log.info("Verticle: " + verticle.getCanonicalName() + " deployed as '" + deploymentName + "'");
        return deploymentName;
    }

    /**
     * Deploy a vertx-guice verticle on a vertx instance with deployment options
     *
     * @param vertx    - Vertx instance to deploy on
     * @param verticle - Verticle class to deploy
     * @param opts     - Deployment options to use for deployment
     * @return - Future that can be used to handle successful / failed deployments
     */
    private static <T extends Verticle> Future<Void> deploy(
            final Vertx vertx,
            final Class<T> verticle,
            final DeploymentOptions opts) {
        Future<Void> done = Future.future();
        String deploymentName = asDeploymentDescriptor(verticle);

        log.info(format("Deploying {0} ...",deploymentName));

        vertx.deployVerticle(deploymentName, opts, r -> {
            if (r.succeeded()) {
                log.info("Successfully deployed verticle: " + deploymentName);
                done.complete();
            } else {
                log.info("Failed to deploy verticle: " + deploymentName);
                done.fail(r.cause());
            }
        });

        return done;
    }

    @Override
    public JsonObject config() {
        JsonObject configuration = context.config();

        ExecutionEnvironment env = ExecutionEnvironment.INSTANCE;

        Map<Supplier<Boolean>,String> options = new LinkedHashMap<>();
        options.put(() -> env.development(),"dev");
        options.put(() -> env.scope().toUpperCase().startsWith("TEST"),"test");
        options.put(() -> true ,DEFAULT_MODE);

        //String environment = System.getProperty(ENVIRONMENT_MODE, DEFAULT_MODE);
        String environment = options.entrySet()
                .stream()
                .filter(entry -> entry.getKey().get())
                .findFirst()
                .flatMap(entry -> Optional.ofNullable(entry.getValue()))
                .orElse(DEFAULT_MODE);

        String prefix = System.getProperty(CONFIG_PREFIX, DEFAULT_CONFIG_PREFIX);
        String configPath = String.format(prefix + File.separator + "conf/%s/app.config.json", environment);
        Path path = Paths.get(configPath);
        log.info("Read custom configuration from: " + path.toFile().getAbsolutePath() );
        //Reading a file over and over is contra performant :(
        Either<String, IOException> readFileEither = Either.fromCatching(
                () -> new String(Files.readAllBytes(path), StandardCharsets.UTF_8),
                IOException.class
        );

        JsonObject environmentConfiguration = readFileEither.fold(
                s -> new JsonObject(s) ,
                e -> new JsonObject("{}")
        );

        return configuration.mergeIn(environmentConfiguration, true);
    }

}
