package io.github.raphaelmun1z.gestao_financeira.exceptions.models;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}