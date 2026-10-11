package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConsultaExtratoReqDTO(
    @NotNull(message = "Informe a data inicial.") LocalDate dataInicial,
    @NotNull(message = "Informe a data final.") LocalDate dataFinal
) {
}
