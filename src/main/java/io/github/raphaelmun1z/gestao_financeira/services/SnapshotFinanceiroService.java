package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.Investimento;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.PagamentoFatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.SnapshotFinanceiroMensal;
import io.github.raphaelmun1z.gestao_financeira.entities.SnapshotInvestimentoMensal;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.PagamentoFaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.SnapshotFinanceiroMensalRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.SnapshotInvestimentoMensalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class SnapshotFinanceiroService {
    private final ContaBancariaRepository contaRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final PagamentoFaturaRepository pagamentoRepository;
    private final SnapshotFinanceiroMensalRepository snapshotRepository;
    private final InvestimentoRepository investimentoRepository;
    private final SnapshotInvestimentoMensalRepository snapshotInvestimentoRepository;

    public SnapshotFinanceiroService(
        ContaBancariaRepository contaRepository,
        RegistroDeMovimentacaoRepository registroRepository,
        PagamentoFaturaRepository pagamentoRepository,
        SnapshotFinanceiroMensalRepository snapshotRepository,
        InvestimentoRepository investimentoRepository,
        SnapshotInvestimentoMensalRepository snapshotInvestimentoRepository
    ) {
        this.contaRepository = contaRepository;
        this.registroRepository = registroRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.snapshotRepository = snapshotRepository;
        this.investimentoRepository = investimentoRepository;
        this.snapshotInvestimentoRepository = snapshotInvestimentoRepository;
    }

    @Transactional
    public void fecharMesAnterior(LocalDate hoje) {
        YearMonth mes = YearMonth.from(hoje).minusMonths(1);
        for (ContaBancaria conta : contaRepository.findAll()) {
            if (conta.getDataAbertura().isAfter(mes.atEndOfMonth())
                || snapshotRepository.findByContaBancaria_IdAndMesReferencia(conta.getId(), mes).isPresent()) {
                continue;
            }
            criarSnapshot(conta, mes, hoje);
        }
        fecharSnapshotsDeInvestimentos(mes, hoje);
    }

    private void criarSnapshot(ContaBancaria conta, YearMonth mes, LocalDate dataFechamento) {
        LocalDate fim = mes.atEndOfMonth();
        LocalDate inicio = mes.atDay(1);
        List<RegistroDeMovimentacao> posteriores = registroRepository
            .findByContaBancaria_IdAndDataLancamentoBetween(conta.getId(), fim.plusDays(1), dataFechamento);
        List<PagamentoFatura> pagamentosPosteriores = pagamentoRepository
            .findByContaBancaria_IdAndDataPagamentoAfter(conta.getId(), fim);
        List<RegistroDeMovimentacao> doMes = registroRepository
            .findByContaBancaria_IdAndDataLancamentoBetween(conta.getId(), inicio, fim);

        BigDecimal saldo = conta.getSaldoCorrente();
        BigDecimal creditoRestante = conta.getCreditoRestante();
        for (RegistroDeMovimentacao registro : posteriores) {
            if (registro.getMetodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO) {
                creditoRestante = creditoRestante.add(registro.getValorTotal());
            } else if (registro.getMetodoPagamento() != MetodoPagamentoEnum.VOUCHER) {
                saldo = saldo.subtract(variacaoDeSaldo(registro));
            }
        }
        for (PagamentoFatura pagamento : pagamentosPosteriores) {
            saldo = saldo.add(pagamento.getValor());
            creditoRestante = creditoRestante.subtract(pagamento.getValor());
        }

        BigDecimal entradas = doMes.stream()
            .filter(registro -> registro.getCategoria().getTipo() == TipoCategoriaEnum.ENTRADA)
            .map(RegistroDeMovimentacao::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saidas = doMes.stream()
            .filter(registro -> registro.getCategoria().getTipo() == TipoCategoriaEnum.SAIDA)
            .map(RegistroDeMovimentacao::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        snapshotRepository.save(new SnapshotFinanceiroMensal(
            conta, mes, dataFechamento, saldo, conta.getCreditoTotal(), creditoRestante, entradas, saidas
        ));
    }

    private BigDecimal variacaoDeSaldo(RegistroDeMovimentacao registro) {
        BigDecimal valor = registro.getValorTotal();
        return registro.getCategoria().getTipo() == TipoCategoriaEnum.ENTRADA ? valor : valor.negate();
    }

    private void fecharSnapshotsDeInvestimentos(YearMonth mes, LocalDate dataFechamento) {
        for (Investimento investimento : investimentoRepository.findAll()) {
            if (investimento.getDataInicio().isAfter(mes.atEndOfMonth())
                || snapshotInvestimentoRepository.findByInvestimento_InvestidorIdAndMesReferencia(
                    investimento.getInvestidor().getId(), mes).stream()
                    .anyMatch(snapshot -> snapshot.getInvestimento().getId().equals(investimento.getId()))) {
                continue;
            }
            snapshotInvestimentoRepository.save(new SnapshotInvestimentoMensal(
                investimento, mes, dataFechamento, valorHistorico(investimento, mes)
            ));
        }
    }

    private BigDecimal valorHistorico(Investimento investimento, YearMonth mes) {
        BigDecimal valor = investimento.getValorCorrente();
        BigDecimal fator = BigDecimal.ONE.add(BigDecimal.valueOf(investimento.getJurosAoMes()).movePointLeft(2));
        YearMonth competencia = investimento.getUltimaCompetenciaJuros();
        while (competencia.isAfter(mes) && competencia.isAfter(YearMonth.from(investimento.getDataInicio()))) {
            valor = valor.divide(fator, 2, java.math.RoundingMode.HALF_UP);
            competencia = competencia.minusMonths(1);
        }
        return valor.max(BigDecimal.ZERO).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}
