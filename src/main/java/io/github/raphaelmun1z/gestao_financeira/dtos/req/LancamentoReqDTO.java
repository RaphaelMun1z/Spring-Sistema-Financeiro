package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoReqDTO(
    @NotNull BigDecimal valor,
    @NotNull LocalDate dataLancamento,
    @NotNull String registroDeMovimentacaoId,
    String faturaId
) {
}
