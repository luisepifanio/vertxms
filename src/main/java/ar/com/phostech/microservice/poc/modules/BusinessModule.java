package ar.com.phostech.microservice.poc.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import ar.com.phostech.microservice.poc.controllers.GreetController;
import io.vertx.core.Vertx;

public class BusinessModule extends AbstractModule {
    protected void configure() {
        this.bind(Dependency.class).to(DependencyImpl.class);
    }

    @Provides
    @Singleton
    public GreetController provideController(Vertx vertx, Dependency service) {
        return new GreetController(vertx, service);
    }
}
