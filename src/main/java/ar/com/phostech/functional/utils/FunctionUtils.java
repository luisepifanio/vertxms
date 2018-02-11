package ar.com.phostech.functional.utils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FunctionUtils {
    // Relocate this utility
    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable, R> R sneakyThrow(Exception t) throws T {
        throw (T) t; // ( ͡° ͜ʖ ͡°)
    }

    static <T, R> Function<T, R> unchecked(ThrowingFunction<T, R> f) {
        return t -> {
            try {
                return f.apply(t);
            } catch (Exception ex) {
                return sneakyThrow(ex);
            }
        };
    }
}
