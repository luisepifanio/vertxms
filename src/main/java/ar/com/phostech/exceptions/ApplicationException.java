package ar.com.phostech.exceptions;

import lombok.Getter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;

import ar.com.phostech.microservice.poc.dto.ErrorDetail;

public class ApplicationException extends RuntimeException {

    @Getter
    final ErrorCatalog catalogEntry;
    @Getter
    final Collection<ErrorDetail> causes;

    public ApplicationException() {
        this(null,null,ErrorCatalog.UNKNOWN,null);
    }

    public ApplicationException(String s) {
        this(s,null,ErrorCatalog.UNKNOWN,null);
    }

    public ApplicationException(Throwable throwable) {
        this(null,throwable,ErrorCatalog.UNKNOWN,null);
    }

    public ApplicationException(String s, Throwable throwable) {
        this(s,throwable,ErrorCatalog.UNKNOWN,null);
    }

    public ApplicationException(String s, Throwable throwable, ErrorCatalog _catalogEntry) {
        this(s,throwable,_catalogEntry,null);
    }

    public ApplicationException(
            String s,
            Throwable throwable,
            ErrorCatalog _catalogEntry,
            Collection<ErrorDetail> _causes) {
        super(
                (s == null)? Optional.ofNullable(_catalogEntry).orElse(ErrorCatalog.INTERNAL_SERVER_ERROR).defaultMessage : s,
                throwable
        );

        catalogEntry = _catalogEntry;
        causes = (_causes == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(_causes) ;
    }

    public ApplicationException(ErrorCatalog _catalogEntry) {
       this(null,null,_catalogEntry,null);
    }

    public static ApplicationExceptionBuilder builder(){
        return new ApplicationExceptionBuilder();
    }

    static class ApplicationExceptionBuilder {
        String message;
        Throwable throwable;
        ErrorCatalog catalogEntry;
        Collection<ErrorDetail> causes;

        public ApplicationExceptionBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ApplicationExceptionBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public ApplicationExceptionBuilder catalogEntry(ErrorCatalog catalogEntry) {
            this.catalogEntry = catalogEntry;
            return this;
        }

        public ApplicationExceptionBuilder causes(Collection<ErrorDetail> _causes) {
            this.causes = _causes;
            return this;
        }

        public ApplicationException build(){
            ErrorCatalog instance = (catalogEntry == null) ? ErrorCatalog.INTERNAL_SERVER_ERROR : catalogEntry;
            return new ApplicationException(message,throwable,instance,causes);
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public ErrorCatalog getCatalogEntry() {
            return catalogEntry;
        }
    }
}
