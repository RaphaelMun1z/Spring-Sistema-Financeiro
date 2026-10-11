package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartaoCreditoReqDTO(
    @NotNull(message = "O limite de crédito é obrigatório!") BigDecimal limiteCredito,
    @NotNull(message = "O dia do fechamento é obrigatório!") int diaFechamento,
    @NotNull(message = "O dia de vencimento é obrigatório!") int diaVencimento
) {
}
