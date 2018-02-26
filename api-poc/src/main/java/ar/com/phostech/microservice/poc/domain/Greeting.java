package ar.com.phostech.microservice.poc.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@JsonDeserialize(builder = Greeting.GreetingBuilder.class)
@Builder( toBuilder = true )
@Value
public class Greeting {
    private static final long serialVersionUID = -8349874823360040605L;

    private final String greeting;
    private final Date date;

}
