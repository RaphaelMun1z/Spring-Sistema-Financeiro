package io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record DashboardFaturaDTO(
    String id,
    String cartaoId,
    String nomeCartao,
    String nomeBanco,
    YearMonth referencia,
    LocalDate dataFechamento,
    LocalDate dataVencimento,
    BigDecimal valorTotal,
    StatusFaturaEnum status
) {
    public DashboardFaturaDTO(Fatura fatura) {
        this(
            fatura.getId(),
            fatura.getCartaoDeCredito().getId(),
            fatura.getCartaoDeCredito().getApelido(),
            fatura.getCartaoDeCredito().getNomeBanco(),
            fatura.getDataReferencia(),
            fatura.getDataFechamento(),
            fatura.getDataVencimento(),
            fatura.getValorTotal(),
            fatura.getStatus()
        );
    }
}
