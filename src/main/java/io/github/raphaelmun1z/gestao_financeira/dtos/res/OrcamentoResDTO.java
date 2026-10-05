package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.orcamento.Orcamento;

import java.math.BigDecimal;
import java.time.YearMonth;

public record OrcamentoResDTO(
    String id,
    YearMonth mesReferencia,
    BigDecimal valorLimite,
    BigDecimal valorCorrente,
    String categoria
) {
    public OrcamentoResDTO(Orcamento orcamento) {
        this(
            orcamento.getId(),
            orcamento.getMesReferencia(),
            orcamento.getValorLimite(),
            orcamento.getValorCorrente(),
            orcamento.getCategoria().getNome()
        );
    }
}