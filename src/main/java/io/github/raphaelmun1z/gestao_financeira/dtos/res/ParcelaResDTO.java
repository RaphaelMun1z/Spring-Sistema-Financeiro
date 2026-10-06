package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaResDTO(
    String id,
    BigDecimal valorParcela,
    String lancamentoId,
    String registroDeDespesasId,
    LocalDate dataLancamento
) {
    public ParcelaResDTO(Parcela parcela) {
        this(
            parcela.getId(),
            parcela.getValorParcela(),
            parcela.getLancamento().getId(),
            parcela.getRegistroDeDespesas().getId(),
            parcela.getDataLancamento()
        );
    }
}
