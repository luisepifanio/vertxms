package ar.com.phostech.microservice.poc.domain;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = Greeting.GreetingBuilder.class)
@Builder
@Value
public class Greeting implements AsBuildable<Greeting.GreetingBuilder> {
    private static final long serialVersionUID = -8349874823360040605L;

    private final String greeting;
    private final Date date;

    public Greeting.GreetingBuilder asBuilder(){
      return builder()
        .greeting(this.greeting)
        .date(this.date)
        ;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class GreetingBuilder { }
}
