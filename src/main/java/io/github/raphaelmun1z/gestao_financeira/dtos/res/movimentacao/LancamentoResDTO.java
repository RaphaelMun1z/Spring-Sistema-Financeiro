package io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResDTO(
    String id,
    BigDecimal valor,
    LocalDate dataLancamento,
    String descricao,
    MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    LocalDate proximaRecorrencia,
    Integer numeroParcela,
    Integer totalParcelas,
    String faturaId,
    String categoriaId,
    String categoriaNome,
    TipoCategoriaEnum tipoCategoria,
    String contaBancariaId,
    String cartaoId,
    String cartaoNome
) {
    public LancamentoResDTO(Lancamento l) {
        this(
            l.getId(),
            l.getValor(),
            l.getDataLancamento(),
            l.getRegistroDeMovimentacao().getDescricao(),
            l.getRegistroDeMovimentacao().getMetodoPagamento(),
            l.getRegistroDeMovimentacao().getEhRecorrente(),
            l.getRegistroDeMovimentacao().getProximaRecorrencia(),
            l.getNumeroParcela(),
            l.getRegistroDeMovimentacao().getQuantidadeParcelas(),
            l.getFatura() == null ? null : l.getFatura().getId(),
            l.getRegistroDeMovimentacao().getCategoria() == null ? null : l.getRegistroDeMovimentacao().getCategoria().getId(),
            l.getRegistroDeMovimentacao().getCategoria() == null ? null : l.getRegistroDeMovimentacao().getCategoria().getNome(),
            l.getRegistroDeMovimentacao().getCategoria() == null ? null : l.getRegistroDeMovimentacao().getCategoria().getTipo(),
            l.getRegistroDeMovimentacao().getContaBancaria() == null ? null : l.getRegistroDeMovimentacao().getContaBancaria().getId(),
            l.getRegistroDeMovimentacao().getCartao() == null ? null : l.getRegistroDeMovimentacao().getCartao().getId(),
            l.getRegistroDeMovimentacao().getCartao() == null ? null : l.getRegistroDeMovimentacao().getCartao().getApelido()
        );
    }
}
