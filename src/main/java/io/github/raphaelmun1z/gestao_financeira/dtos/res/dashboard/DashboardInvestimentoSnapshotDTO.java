package io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard;

import io.github.raphaelmun1z.gestao_financeira.entities.SnapshotInvestimentoMensal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DashboardInvestimentoSnapshotDTO(
    String investimentoId,
    String categoria,
    LocalDate dataFechamento,
    BigDecimal valorFinal
) {
    public DashboardInvestimentoSnapshotDTO(SnapshotInvestimentoMensal snapshot) {
        this(
            snapshot.getInvestimento().getId(),
            snapshot.getInvestimento().getCategoria(),
            snapshot.getDataFechamento(),
            snapshot.getValorFinal()
        );
    }
}
