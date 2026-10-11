package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;

public record FaturaReqDTO(
    @NotNull(message = "O valor total é obrigatório!") BigDecimal valorTotal,
    @NotNull(message = "A data referência é obrigatória!") YearMonth dataReferencia,
    StatusFaturaEnum status
) {
}
