package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroDeMovimentacaoResDTO(
    String id,
    BigDecimal valorTotal,
    LocalDate dataLancamento,
    String descricao,
    MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    int qntParcelas,
    String categoriaId,
    String contaBancariaId
) {
}

