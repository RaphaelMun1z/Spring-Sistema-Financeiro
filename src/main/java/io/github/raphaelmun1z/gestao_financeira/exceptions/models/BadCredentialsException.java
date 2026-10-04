package io.github.raphaelmun1z.gestao_financeira.exceptions.models;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String msg) {
        super(msg);
    }
}