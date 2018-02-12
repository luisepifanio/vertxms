package ar.com.phostech.microservice.poc.modules;

import ar.com.phostech.microservice.poc.domain.Greeting;

public interface Dependency {
    Greeting getGreetingMessage(String name);
}
