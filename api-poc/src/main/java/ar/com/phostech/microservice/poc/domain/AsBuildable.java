package ar.com.phostech.microservice.poc.domain;

import java.io.Serializable;

public interface AsBuildable<T> extends Serializable {
    BuilderOf<T> toBuilder();
}
