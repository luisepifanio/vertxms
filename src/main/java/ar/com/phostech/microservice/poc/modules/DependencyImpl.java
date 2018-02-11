package ar.com.phostech.microservice.poc.modules;

import java.util.Date;

import ar.com.phostech.microservice.poc.domain.Greeting;

import static java.text.MessageFormat.format;

class DependencyImpl implements Dependency {
    @Override
    public Greeting getGreetingMessage(final String _name) {
        String name = ( _name == null || _name.length() == 0 ) ? "Jonh Doe" : _name;
        return Greeting.builder()
                .greeting(format("Hi {0} from vertx-guice launcher example.",name) )
                .date(new Date())
            .build()
         ;
    }
}
