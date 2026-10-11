package io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResDTO(
    BigDecimal valor,
    LocalDate dataLancamento,
    String descricao
) {
    public LancamentoResDTO(Lancamento l) {
        this(l.getValor(), l.getDataLancamento(), l.getRegistroDeMovimentacao().getDescricao());
    }
}
