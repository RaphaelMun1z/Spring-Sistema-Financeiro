package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoReqDTO(
    @NotBlank(message = "A 'categoria' é obrigatória!") String categoria,
    LocalDate dataInicio,
    BigDecimal valorInicial,
    BigDecimal valorCorrente,
    @NotNull(message = "O 'jurosAoMes' é obrigatório!") Float jurosAoMes
) {
    public InvestimentoReqDTO {
        if (dataInicio == null) {
            dataInicio = LocalDate.now();
        }

        if (valorInicial == null) {
            valorInicial = BigDecimal.ZERO;
        }

        if (valorCorrente == null) {
            valorCorrente = BigDecimal.ZERO;
        }
    }
}
