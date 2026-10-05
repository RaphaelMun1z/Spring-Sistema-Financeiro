package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;

import java.math.BigDecimal;
import java.util.Date;

public record LancamentoResDTO(
    String id,
    BigDecimal valorTotal,
    Date dataLancamento,
    String descricao,
    MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    String categoria,
    String nomeBanco,
    String apelidoCartao
) {
    public LancamentoResDTO(Lancamento lancamento) {
        this(
            lancamento.getId(),
            lancamento.getValorTotal(),
            lancamento.getDataLancamento(),
            lancamento.getDescricao(),
            lancamento.getMetodoPagamento(),
            lancamento.getEhRecorrente(),
            lancamento.getCategoria().getNome(),
            lancamento.getContaBancaria().getNomeDoBanco(),
            lancamento.getCartao() != null
                ? lancamento.getCartao().getApelido()
                : null
        );
    }
}
