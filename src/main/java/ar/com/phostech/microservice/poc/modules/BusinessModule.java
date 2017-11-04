package ar.com.phostech.microservice.poc.modules;

import com.google.inject.AbstractModule;

public class BusinessModule extends AbstractModule {
    protected void configure() {
        this.bind(Dependency.class).to(DependencyImpl.class);
    }
}
