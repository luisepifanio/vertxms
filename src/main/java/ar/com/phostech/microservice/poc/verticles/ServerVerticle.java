package ar.com.phostech.microservice.poc.verticles;

import ar.com.phostech.microservice.poc.config.ApplicationPaths;
import ar.com.phostech.microservice.poc.controllers.GlobalHandlers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by levontamrazov on 2017-01-28.
 */
public class ServerVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> future) throws Exception{
        int PORT = 8080;

        Router mainRouter = Router.router(vertx);
        mainRouter.route().consumes("application/json");
        mainRouter.route().produces("application/json");

        Set<String> allowHeaders = getAllowedHeaders();
        Set<HttpMethod> allowMethods = getAllowedMethods();
        mainRouter.route().handler(BodyHandler.create());
        mainRouter.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        mainRouter.get(ApplicationPaths.PING).handler(GlobalHandlers::lbCheck);
        mainRouter.route().failureHandler(GlobalHandlers::error);

        // Create the http server and pass it the router
        vertx.createHttpServer()
            .requestHandler(mainRouter::accept)
            .listen(PORT, res -> {
                if(res.succeeded()){
                    System.out.println("Server listening on port " + PORT);
                    future.complete();
                }
                else{
                    System.out.println("Failed to launch server");
                    future.fail(res.cause());
                }
            });
    }

    private Set<String> getAllowedHeaders(){

        return Stream.of(
                    "x-requested-with",
                    HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN ,
                    HttpHeaders.ORIGIN,
                    HttpHeaders.CONTENT_TYPE,
                    HttpHeaders.ACCEPT
            ).map( sec -> sec.toString())
            .collect(Collectors.toCollection(HashSet::new));
    }

    private Set<HttpMethod> getAllowedMethods(){
        return Stream.of(
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.DELETE,
            HttpMethod.PATCH
        ).collect(Collectors.toCollection(HashSet::new));
    }
}
