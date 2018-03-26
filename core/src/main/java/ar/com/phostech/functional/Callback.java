package ar.com.phostech.functional;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface Callback<T> {

    static <T> Callback<T> of(Consumer<T> success, Consumer<Throwable> failure) {
        return new DelegatingCallback<>(success, failure);
    }

    void success(T t);

    void failure(Throwable throwable);
}
