package ar.com.phostech.microservice.poc.controllers;

import static ar.com.phostech.microservice.poc.config.ApplicationPaths.GREET;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import ar.com.phostech.microservice.poc.config.ApplicationPaths;
import ar.com.phostech.microservice.poc.modules.Dependency;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class GreetController {

    private static final Logger log = LoggerFactory.getLogger(GreetController.class);

    private final Vertx vertx;
    private final Dependency service;
    private final Router router;

    /**
     * Takes an instance of Vertx for blocking calls and
     * the service it should be using.
     * @param vertx - Vertx instance
     * @param service - HelloWorld service
     */
    @Inject
    public GreetController(Vertx vertx, Dependency service) {
        this.vertx = Preconditions.checkNotNull(vertx);
        this.service = Preconditions.checkNotNull(service);
        this.router = Router.router(vertx);
        setupRouter();
    }

    private void setupRouter() {
        log.info("setupRouter");
        router.get(ApplicationPaths.GREET).handler(this::getGreeting);
        router.get(ApplicationPaths.GREETW).handler(this::getGreetingW);
    }

    /**
     * Return a configured Router instance
     * @return - vertx Router
     */
    public Router getRouter() {
        return router;
    }

    /**
     * Handler that uses executeBlocking to process request
     * async
     * @param ctx - RoutingContext for the request
     */
    private void getGreeting(RoutingContext ctx) {
        log.info("getGreeting .. ");
        String name = ctx.request().getParam("name");
        vertx.executeBlocking(fut -> {
            fut.complete(service.getGreetingMessage(name));
        }, false, // IMPORTANT TO MAKE THIS FALSE
                res -> {
                    handleAsyncResponse(res, ctx);
                });
    }

    /**
     * Handler that uses the event bus and a worker verticle
     * to process the request async
     * @param ctx - RoutingContext for the request
     */
    private void getGreetingW(RoutingContext ctx) {
        log.info("getGreetingW .. ");

        String data = new JsonObject().put("name", ctx.request().getParam("name")).encode();

        vertx.eventBus().send(GREET, data, res -> {
            handleEventBusResponse(res, ctx);
        });
    }

    /**
     * Helper method for handling executeBlocking responses
     * @param res - AsynResult from executeBlocking
     * @param ctx - RoutingContext for the request
     */
    private void handleAsyncResponse(AsyncResult<Object> res, RoutingContext ctx) {
        // Handler for the future. If successful, encode result and send
        if (res.succeeded()) {
            try {
                ctx.response().end(Json.encode(res.result()));
            } catch (EncodeException e) {
                ctx.fail(new RuntimeException("Failed to encode results."));
            }

        } else {
            ctx.fail(res.cause());
        }
    }

    /**
     * Helper method for handling event bus responses
     * @param res - Event bus response
     * @param ctx - RoutingContext for the request
     */
    private void handleEventBusResponse(AsyncResult<Message<Object>> res, RoutingContext ctx) {
        if (res.succeeded()) {
            ctx.response().end(res.result().body().toString());
        } else {
            ctx.fail(res.cause());
        }
    }
}
