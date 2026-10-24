package io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.SnapshotFinanceiroMensal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardContaSnapshotDTO(
    String contaBancariaId,
    String nomeDoBanco,
    LocalDate dataFechamento,
    BigDecimal saldoFinal,
    BigDecimal creditoTotal,
    BigDecimal creditoUsado,
    BigDecimal creditoRestante,
    BigDecimal totalEntradas,
    BigDecimal totalSaidas
) {
    public DashboardContaSnapshotDTO(SnapshotFinanceiroMensal snapshot) {
        this(
            snapshot.getContaBancaria().getId(),
            snapshot.getContaBancaria().getNomeDoBanco(),
            snapshot.getDataFechamento(),
            snapshot.getSaldoFinal(),
            snapshot.getCreditoTotal(),
            snapshot.getCreditoTotal().subtract(snapshot.getCreditoRestante()),
            snapshot.getCreditoRestante(),
            snapshot.getTotalEntradas(),
            snapshot.getTotalSaidas()
        );
    }
}
