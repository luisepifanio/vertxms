package ar.com.phostech.microservice.poc.modules;

import java.util.Date;

import ar.com.phostech.microservice.poc.domain.Greeting;

class DependencyImpl implements Dependency {
    @Override
    public Greeting getGreetingMessage(final String _name) {
        String name = ( _name == null || _name.length() == 0 ) ? "Jonh Doe" : _name;
        return Greeting.builder()
                .greeting("Hi" + name + " from vertx-guice launcher example.")
                .date(new Date())
            .build()
         ;
    }
}
