package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaReqDTO(
    BigDecimal valorParcela,
    String lancamentoId,
    LocalDate dataLancamento,
    String registroDeDespesasId
) {
    public ParcelaReqDTO(BigDecimal valorParcela, String lancamentoId, LocalDate dataLancamento){
        this(valorParcela, lancamentoId, dataLancamento, null);
    }

    public ParcelaReqDTO(BigDecimal valorParcela, String lancamentoId){
        this(valorParcela, lancamentoId, LocalDate.now(), null);
    }
}
