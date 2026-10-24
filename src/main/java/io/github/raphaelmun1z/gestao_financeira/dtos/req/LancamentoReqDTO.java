package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoReqDTO(
    @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal valor,
    @NotNull LocalDate dataLancamento,
    @NotBlank String registroDeMovimentacaoId,
    String faturaId
) {
}
