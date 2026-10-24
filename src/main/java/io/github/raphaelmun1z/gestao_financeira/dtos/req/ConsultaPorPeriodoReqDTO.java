package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDate;

public record ConsultaPorPeriodoReqDTO(
    @Schema(description = "Data inicial inclusiva do período no formato ISO-8601.", example = "2026-01-01", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Informe a data inicial.") LocalDate dataInicial,
    @Schema(description = "Data final inclusiva do período no formato ISO-8601.", example = "2026-01-31", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Informe a data final.") LocalDate dataFinal
) {
    @AssertTrue(message = "A data final deve ser igual ou posterior à data inicial.")
    public boolean isPeriodoValido() {
        return dataInicial == null || dataFinal == null || !dataFinal.isBefore(dataInicial);
    }
}
