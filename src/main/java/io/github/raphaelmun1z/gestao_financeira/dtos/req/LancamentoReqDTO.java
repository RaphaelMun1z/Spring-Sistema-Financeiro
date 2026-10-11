package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoReqDTO(
    @NotNull BigDecimal valor,
    @NotNull LocalDate dataLancamento,
    String registroDeMovimentacaoId,
    String faturaId
) {
}
