package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.MetaFinanceira;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MetaFinanceiraResDTO(
    String id,
    String objetivo,
    BigDecimal valorAlvo,
    BigDecimal valorAcumulado,
    YearMonth dataConclusaoPrevista
) {
    public MetaFinanceiraResDTO(MetaFinanceira metaFinanceira) {
        this(
            metaFinanceira.getId(),
            metaFinanceira.getObjetivo(),
            metaFinanceira.getValorAlvo(),
            metaFinanceira.getValorAcumulado(),
            metaFinanceira.getDataConclusaoPrevista()
        );
    }
}
