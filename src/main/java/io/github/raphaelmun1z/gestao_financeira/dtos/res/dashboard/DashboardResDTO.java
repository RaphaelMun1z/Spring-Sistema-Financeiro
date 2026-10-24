package io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record DashboardResDTO(
    YearMonth mesReferencia,
    BigDecimal saldoTotal,
    BigDecimal creditoTotal,
    BigDecimal creditoUsado,
    BigDecimal creditoRestante,
    DashboardResumoFaturasDTO faturas,
    List<DashboardFaturaDTO> proximasFaturas,
    List<ContaBancariaResDTO> contas,
    List<RegistroMovimentacaoResDTO> ultimasMovimentacoes,
    List<LancamentoResDTO> ultimosLancamentos,
    List<InvestimentoResDTO> investimentos,
    List<MetaFinanceiraResDTO> metas,
    List<OrcamentoResDTO> orcamentos,
    boolean snapshotHistoricoDisponivel,
    List<DashboardContaSnapshotDTO> contasNoMes,
    boolean snapshotInvestimentosHistoricoDisponivel,
    List<DashboardInvestimentoSnapshotDTO> investimentosNoMes
) {
}
