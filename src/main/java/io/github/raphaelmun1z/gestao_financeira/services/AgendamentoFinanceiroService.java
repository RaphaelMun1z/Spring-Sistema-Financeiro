package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;

@Service
public class AgendamentoFinanceiroService {
    private final CartaoRepository cartaoRepository;
    private final FaturaRepository faturaRepository;
    private final InvestimentoRepository investimentoRepository;
    private final RegistroDeMovimentacaoService registroDeMovimentacaoService;
    private final SnapshotFinanceiroService snapshotFinanceiroService;
    private final ZoneId zoneId;

    public AgendamentoFinanceiroService(
        CartaoRepository cartaoRepository,
        FaturaRepository faturaRepository,
        InvestimentoRepository investimentoRepository,
        RegistroDeMovimentacaoService registroDeMovimentacaoService,
        SnapshotFinanceiroService snapshotFinanceiroService,
        @Value("${app.agendamento.financeiro.zone}") String zone
    ) {
        this.cartaoRepository = cartaoRepository;
        this.faturaRepository = faturaRepository;
        this.investimentoRepository = investimentoRepository;
        this.registroDeMovimentacaoService = registroDeMovimentacaoService;
        this.snapshotFinanceiroService = snapshotFinanceiroService;
        this.zoneId = ZoneId.of(zone);
    }

    @Scheduled(
        cron = "${app.agendamento.financeiro.cron}",
        zone = "${app.agendamento.financeiro.zone}"
    )
    @Transactional
    public void executarRotinasDiarias() {
        LocalDate hoje = LocalDate.now(zoneId);
        YearMonth mesAtual = YearMonth.from(hoje);
        criarFaturasDoMes(mesAtual);
        atualizarStatusDasFaturas(hoje);
        processarRecargasDeVoucher(hoje, mesAtual);
        registroDeMovimentacaoService.processarRecorrencias(hoje);
        processarRendimentos(hoje);
        snapshotFinanceiroService.fecharMesAnterior(hoje);
    }

    void criarFaturasDoMes(YearMonth mes) {
        for (CartaoCredito cartao : cartaoRepository.findTodosCartoesDeCredito()) {
            faturaRepository.findByCartaoDeCredito_IdAndDataReferencia(cartao.getId(), mes)
                .orElseGet(() -> faturaRepository.save(new Fatura(mes, cartao)));
        }
    }

    void atualizarStatusDasFaturas(LocalDate hoje) {
        for (Fatura fatura : faturaRepository.findByStatusNot(StatusFaturaEnum.PAGA)) {
            faturaRepository.atualizarStatusSeNaoPaga(
                fatura.getId(), fatura.calcularStatus(hoje), StatusFaturaEnum.PAGA);
        }
    }

    void processarRecargasDeVoucher(LocalDate hoje, YearMonth mesAtual) {
        for (CartaoVoucher voucher : cartaoRepository.findTodosCartoesVoucher()) {
            int dia = Math.min(voucher.getDiaRecarga(), mesAtual.lengthOfMonth());
            if (!hoje.isBefore(mesAtual.atDay(dia))) {
                voucher.recarregar(mesAtual);
            }
        }
    }

    void processarRendimentos(LocalDate hoje) {
        investimentoRepository.findTodosParaAgendamento()
            .forEach(investimento -> investimento.aplicarRendimentosAte(hoje));
    }
}
