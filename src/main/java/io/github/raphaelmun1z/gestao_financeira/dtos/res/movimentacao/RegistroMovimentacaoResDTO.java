package io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroMovimentacaoResDTO(
    String id,
    BigDecimal valorTotal,
    LocalDate dataLancamento,
    String descricao,
    MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    @Schema(description = "Próxima data prevista para geração automática; nulo quando não recorrente.") LocalDate proximaRecorrencia,
    Integer qntParcelas,
    String categoriaId,
    String contaBancariaId
) {
    public RegistroMovimentacaoResDTO(RegistroDeMovimentacao rm) {
        this(
            rm.getId(),
            rm.getValorTotal(),
            rm.getDataLancamento(),
            rm.getDescricao(),
            rm.getMetodoPagamento(),
            rm.getEhRecorrente(),
            rm.getProximaRecorrencia(),
            rm.getQuantidadeParcelas(),
            rm.getCategoria().getId(),
            rm.getContaBancaria().getId()
        );
    }

}

