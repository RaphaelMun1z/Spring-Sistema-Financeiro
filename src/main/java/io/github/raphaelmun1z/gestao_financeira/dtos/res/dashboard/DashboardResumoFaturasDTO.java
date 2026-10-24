package io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard;

import java.math.BigDecimal;

public record DashboardResumoFaturasDTO(
    long total,
    long abertas,
    long fechadas,
    long vencidas,
    long pagas,
    BigDecimal valorPendente,
    BigDecimal valorVencido
) {
}
