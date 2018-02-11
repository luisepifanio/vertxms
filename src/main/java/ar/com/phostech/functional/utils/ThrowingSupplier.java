package ar.com.phostech.functional.utils;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
