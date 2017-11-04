package ar.com.phostech.microservice.poc.verticles;

import com.google.inject.Inject;

import ar.com.phostech.microservice.poc.domain.Greeting;
import ar.com.phostech.microservice.poc.modules.Dependency;

import static ar.com.phostech.microservice.poc.config.Events.GREET;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

/**
 * Created by levontamrazov on 2017-02-02.
 * Worker verticle that will process requests through the event
 * bus
 */
public class GreeterVerticle extends AbstractVerticle{
    private final Dependency service;

    /**
     * Constructor takes a service this verticle should be using.
     * @param service - Dependency instance
     */
    @Inject
    public GreeterVerticle(Dependency service) {
        this.service = service;
    }

    /**
     * Start method gets called when the verticle is deployed.
     *
     * @param done
     */
    @Override
    public void start(Future<Void> done){
        // Create a message consumer of type String
        // and set it to listen on the GREET event.
        MessageConsumer<String> consumer= vertx.eventBus().consumer(GREET);

        // Handler for the event
        consumer.handler(m -> {
            // Parse the body into a json object, and call the service with
            // the required parameters
            JsonObject data = new JsonObject(m.body());
            Greeting result = service.getGreetingMessage();

            // reply to the sender or fail the message.
            try{
                m.reply(Json.encode(result));
            }catch (EncodeException e){
                m.fail(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "Failed to encode data.");
            }
        });

        done.complete();
    }
}
