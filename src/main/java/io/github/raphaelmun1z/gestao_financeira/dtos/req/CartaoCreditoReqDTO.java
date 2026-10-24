package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartaoCreditoReqDTO(
    @NotNull(message = "O limite de crédito é obrigatório!") @Positive @Digits(integer = 17, fraction = 2) BigDecimal limiteCredito,
    @NotNull(message = "O dia do fechamento é obrigatório!") @Min(1) @Max(31) Integer diaFechamento,
    @NotNull(message = "O dia de vencimento é obrigatório!") @Min(1) @Max(31) Integer diaVencimento
) {
}
