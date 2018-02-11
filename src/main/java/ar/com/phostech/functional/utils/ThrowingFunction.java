package ar.com.phostech.functional.utils;

public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
