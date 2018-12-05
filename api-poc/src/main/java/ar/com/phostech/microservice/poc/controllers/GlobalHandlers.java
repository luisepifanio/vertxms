package ar.com.phostech.microservice.poc.controllers;

import com.google.common.net.MediaType;

import ar.com.phostech.exceptions.ApplicationException;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GlobalHandlers {
    private GlobalHandlers(){}

    public static void lbCheck(RoutingContext ctx){
        ctx.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString())
            .end("pong");
    }

    public static void error(RoutingContext ctx){
        int status;
        String msg;

        // Get thrown exception from context
        Throwable failure = ctx.failure();

        if(ApplicationException.class.isAssignableFrom(failure.getClass())){
            ApplicationException appExc = (ApplicationException) failure;
            msg = appExc.getMessage();
            status = appExc.getCatalogEntry().status;
        }
        else {
            System.out.println(failure);
            msg = "Sorry, something went wrong";
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
        }

        // Log the error, and send a json encoded response.
        JsonObject res = new JsonObject().put("status", status).put("message", msg);
        ctx.response().setStatusCode(status).end(res.encode());
    }
}
