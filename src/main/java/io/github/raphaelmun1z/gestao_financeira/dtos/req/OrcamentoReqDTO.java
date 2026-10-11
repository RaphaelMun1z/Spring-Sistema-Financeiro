package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrcamentoReqDTO(
    @NotNull(message = "O 'valorLimite' é obrigatório!") BigDecimal valorLimite,
    BigDecimal valorCorrente,
    @NotNull(message = "A 'categoriaId' é obrigatória!") String categoriaId
) {
    public OrcamentoReqDTO {
        if (valorCorrente == null) {
            valorCorrente = BigDecimal.ZERO;
        }
    }
}
