package io.github.raphaelmun1z.gestao_financeira.exceptions.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class HistoricalDataUnavailableException extends RuntimeException {
    public HistoricalDataUnavailableException(String message) {
        super(message);
    }
}
