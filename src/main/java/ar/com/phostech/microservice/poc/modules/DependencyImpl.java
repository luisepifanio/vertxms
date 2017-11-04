package ar.com.phostech.microservice.poc.modules;

import java.util.Date;

import ar.com.phostech.microservice.poc.domain.Greeting;

class DependencyImpl implements Dependency {
    @Override
    public Greeting getGreetingMessage() {
        return Greeting.builder()
                .greeting("Hi all from vertx-guice launcher example.")
                .date(new Date())
            .build()
         ;
    }
}
