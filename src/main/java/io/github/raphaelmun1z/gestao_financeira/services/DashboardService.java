package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardFaturaDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardContaSnapshotDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardInvestimentoSnapshotDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.PeriodosHistoricoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardResumoFaturasDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.HistoricalDataUnavailableException;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.MetaFinanceiraRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.OrcamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.SnapshotFinanceiroMensalRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.SnapshotInvestimentoMensalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final ContaBancariaRepository contaRepository;
    private final FaturaRepository faturaRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final LancamentoRepository lancamentoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final MetaFinanceiraRepository metaRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final SnapshotFinanceiroMensalRepository snapshotRepository;
    private final SnapshotInvestimentoMensalRepository snapshotInvestimentoRepository;

    public DashboardService(
        ContaBancariaRepository contaRepository,
        FaturaRepository faturaRepository,
        RegistroDeMovimentacaoRepository registroRepository,
        LancamentoRepository lancamentoRepository,
        InvestimentoRepository investimentoRepository,
        MetaFinanceiraRepository metaRepository,
        OrcamentoRepository orcamentoRepository,
        SnapshotFinanceiroMensalRepository snapshotRepository,
        SnapshotInvestimentoMensalRepository snapshotInvestimentoRepository
    ) {
        this.contaRepository = contaRepository;
        this.faturaRepository = faturaRepository;
        this.registroRepository = registroRepository;
        this.lancamentoRepository = lancamentoRepository;
        this.investimentoRepository = investimentoRepository;
        this.metaRepository = metaRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.snapshotRepository = snapshotRepository;
        this.snapshotInvestimentoRepository = snapshotInvestimentoRepository;
    }

    @Transactional
    public DashboardResDTO consultar(YearMonth mesReferencia) {
        String usuarioId = UsuarioService.obterUsuarioAutenticado().getId();
        YearMonth mesAtual = mesReferencia == null ? YearMonth.now() : mesReferencia;
        LocalDate dataInicial = mesAtual.atDay(1);
        LocalDate dataFinal = mesAtual.atEndOfMonth();

        List<ContaBancaria> contasEntidade = contaRepository.findByTitularId(usuarioId);
        List<DashboardContaSnapshotDTO> contasNoMes = snapshotRepository
            .findByContaBancaria_TitularIdAndMesReferencia(usuarioId, mesAtual)
            .stream().map(DashboardContaSnapshotDTO::new).toList();
        boolean snapshotHistoricoDisponivel = contasEntidade.isEmpty() || contasNoMes.size() == contasEntidade.size();
        if (!snapshotHistoricoDisponivel) {
            throw new HistoricalDataUnavailableException(
                "Não há saldo histórico disponível para o período " + mesAtual + "."
            );
        }
        List<Fatura> faturas = faturaRepository.findByTitularId(usuarioId).stream()
            .filter(fatura -> mesAtual.equals(fatura.getDataReferencia()))
            .toList();
        faturas.forEach(Fatura::atualizarStatus);

        BigDecimal saldoTotal = contasNoMes.stream()
            .map(DashboardContaSnapshotDTO::saldoFinal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoTotal = contasNoMes.stream()
            .map(DashboardContaSnapshotDTO::creditoTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal creditoRestante = contasNoMes.stream()
            .map(DashboardContaSnapshotDTO::creditoRestante).reduce(BigDecimal.ZERO, BigDecimal::add);

        DashboardResumoFaturasDTO resumo = criarResumoFaturas(faturas);
        List<DashboardFaturaDTO> proximas = faturas.stream()
            .filter(fatura -> fatura.getStatus() != StatusFaturaEnum.PAGA)
            .sorted(Comparator.comparing(Fatura::getDataVencimento))
            .limit(5)
            .map(DashboardFaturaDTO::new)
            .toList();

        List<InvestimentoResDTO> investimentos = investimentoRepository.findByInvestidorId(usuarioId)
            .stream().map(InvestimentoResDTO::new).toList();
        List<DashboardInvestimentoSnapshotDTO> investimentosNoMes = snapshotInvestimentoRepository
            .findByInvestimento_InvestidorIdAndMesReferencia(usuarioId, mesAtual)
            .stream().map(DashboardInvestimentoSnapshotDTO::new).toList();
        boolean snapshotInvestimentosHistoricoDisponivel = investimentos.isEmpty()
            || investimentosNoMes.size() == investimentos.size();
        List<MetaFinanceiraResDTO> metas = metaRepository.findByCriadorId(usuarioId)
            .stream().map(MetaFinanceiraResDTO::new).toList();
        List<OrcamentoResDTO> orcamentos = orcamentoRepository.findByCategoria_AutorIdAndMesReferencia(usuarioId, mesAtual)
            .stream().map(OrcamentoResDTO::new).toList();

        return new DashboardResDTO(
            mesAtual,
            saldoTotal,
            creditoTotal,
            creditoTotal.subtract(creditoRestante),
            creditoRestante,
            resumo,
            proximas,
            contasEntidade.stream().map(ContaBancariaResDTO::new).toList(),
            registroRepository.findByContaBancaria_TitularIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(
                    usuarioId, dataInicial, dataFinal).stream().limit(10)
                .map(RegistroMovimentacaoResDTO::new).toList(),
            lancamentoRepository.findByRegistroDeMovimentacao_ContaBancaria_TitularIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(
                    usuarioId, dataInicial, dataFinal).stream().limit(10)
                .map(LancamentoResDTO::new).toList(),
            investimentos,
            metas,
            orcamentos,
            snapshotHistoricoDisponivel,
            contasNoMes,
            snapshotInvestimentosHistoricoDisponivel,
            investimentosNoMes
        );
    }

    @Transactional(readOnly = true)
    public PeriodosHistoricoResDTO consultarPeriodosComHistorico() {
        String usuarioId = UsuarioService.obterUsuarioAutenticado().getId();
        int quantidadeContas = contaRepository.findByTitularId(usuarioId).size();

        if (quantidadeContas == 0) {
            return new PeriodosHistoricoResDTO(List.of());
        }

        List<YearMonth> periodos = snapshotRepository.findByContaBancaria_TitularId(usuarioId).stream()
            .collect(Collectors.groupingBy(
                snapshot -> snapshot.getMesReferencia(),
                Collectors.mapping(
                    snapshot -> snapshot.getContaBancaria().getId(),
                    Collectors.toSet()
                )
            ))
            .entrySet().stream()
            .filter(entry -> entry.getValue().size() == quantidadeContas)
            .map(Map.Entry::getKey)
            .sorted(Comparator.reverseOrder())
            .toList();

        return new PeriodosHistoricoResDTO(periodos);
    }

    private DashboardResumoFaturasDTO criarResumoFaturas(List<Fatura> faturas) {
        BigDecimal pendente = faturas.stream()
            .filter(fatura -> fatura.getStatus() != StatusFaturaEnum.PAGA)
            .map(Fatura::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal vencido = faturas.stream()
            .filter(fatura -> fatura.getStatus() == StatusFaturaEnum.VENCIDA)
            .map(Fatura::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new DashboardResumoFaturasDTO(
            faturas.size(),
            contar(faturas, StatusFaturaEnum.ABERTA),
            contar(faturas, StatusFaturaEnum.FECHADA),
            contar(faturas, StatusFaturaEnum.VENCIDA),
            contar(faturas, StatusFaturaEnum.PAGA),
            pendente,
            vencido
        );
    }

    private long contar(List<Fatura> faturas, StatusFaturaEnum status) {
        return faturas.stream().filter(fatura -> fatura.getStatus() == status).count();
    }
}
