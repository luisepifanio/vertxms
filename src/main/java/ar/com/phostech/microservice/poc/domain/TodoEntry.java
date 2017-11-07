package ar.com.phostech.microservice.poc.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

@JsonDeserialize(builder = TodoEntry.TodoEntryBuilder.class)
@Builder
@Value
public class TodoEntry {
    private final Integer id;

    private final String title;

    private final boolean completed;

    private final int ordering;

    private final String url;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class TodoEntryBuilder {
    }
}
