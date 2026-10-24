package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record OrcamentoReqDTO(
    @NotNull(message = "O 'valorLimite' é obrigatório!") @Positive @Digits(integer = 17, fraction = 2) BigDecimal valorLimite,
    @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal valorCorrente
) {
    public OrcamentoReqDTO {
        if (valorCorrente == null) {
            valorCorrente = BigDecimal.ZERO;
        }
    }
}
