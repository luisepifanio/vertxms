package ar.com.phostech.functional;

import ar.com.phostech.functional.utils.ThrowingSupplier;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ar.com.phostech.functional.utils.FunctionUtils.sneakyThrow;

public class Either<S, F> implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(Either.class);

    private final S expected;
    private final F alternative;

    public static <A, B> Either<A, B> successOf(A success) {
        return new Either<>(success, null);
    }

    public static <A, B> Either<A, B> failureOf(B failure) {
        return new Either<>(null, failure);
    }

    private Either(S s, F f) {
        super();

        expected = s;
        alternative = f;

        // Defensive assumptions
        if (expected != null && alternative != null) {
            throw new IllegalStateException("Both values cannot be set");
        } else if (expected == null && alternative == null) {
            throw new IllegalStateException("Both values cannot be null");
        }
    }

    public boolean hasExpected() {
        return expected != null;
    }

    public boolean hasAlternative() {
        return alternative != null;
    }

    public S expected() {
        return expected;
    }

    public F alternative() {
        return alternative;
    }

    public <C> C fold(Function<S, C> functionOnExpected, Function<F, C> functionOnAlternative) {
        if (hasExpected()) {
            return functionOnExpected.apply(expected());
        } else {
            return functionOnAlternative.apply(alternative());
        }
    }

    public void consumeOn(Callback<S> callback) {
        Objects.requireNonNull(callback, "callback could not be null");
        if (hasExpected()) {
            callback.success(expected());
        } else if (alternative() instanceof Throwable) {
            Throwable throwable = ((Throwable) alternative());
            callback.failure(throwable);
        } else {
            log.warn("alternative is not bound to a Throwable ({0})", alternative());
            callback.failure(null);
        }
    }

    @SuppressWarnings("unchecked")
    public static <A, B extends Throwable> Either<A, B> fromCatching(final ThrowingSupplier<A> code, final Class<B>... exceptionTypes) {
        Objects.requireNonNull(code, "code could not be null");
        Objects.requireNonNull(exceptionTypes, "exceptionType could not be null");
        List<Class<B>> classes = Stream.of(exceptionTypes)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (classes.isEmpty()) {
            throw new IllegalStateException("Exception types should contains at least one element");
        }

        try {
            return Either.successOf(code.get());
        } catch (Exception throwable) {
            boolean matches = classes.stream()
                    .anyMatch(et -> et.isAssignableFrom(throwable.getClass()));
            if (matches) {
                return Either.failureOf((B) throwable);
            }
            log.warn(">> Something went wrong", throwable);
            // Don't like to re-throw an exception!
            sneakyThrow(throwable);
        }
        return null; // Hope this is won't happens
    }

}
