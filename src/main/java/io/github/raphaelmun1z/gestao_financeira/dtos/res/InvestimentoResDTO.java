package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.investimento.Investimento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoResDTO(
    String id,
    String categoria,
    LocalDate dataInicio,
    BigDecimal valorInicial,
    BigDecimal valorCorrente,
    Float jurosAoMes
) {
    public InvestimentoResDTO(Investimento investimento) {
        this(
            investimento.getId(),
            investimento.getCategoria(),
            investimento.getDataInicio(),
            investimento.getValorInicial(),
            investimento.getValorCorrente(),
            investimento.getJurosAoMes()
        );
    }
}
