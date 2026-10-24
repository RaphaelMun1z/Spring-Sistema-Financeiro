package io.github.raphaelmun1z.gestao_financeira;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class FaturaTests {
    @Test
    void respeitarFechamentoVencimentoViradaDeAnoEAnosBissextos() {
        CartaoCredito cartao = new CartaoCredito("Crédito", null, BigDecimal.TEN, 31, 5);
        assertThat(cartao.calcularReferencia(LocalDate.of(2024, 1, 30))).isEqualTo(YearMonth.of(2024, 2));
        assertThat(cartao.calcularReferencia(LocalDate.of(2024, 1, 31))).isEqualTo(YearMonth.of(2024, 3));
        assertThat(cartao.calcularReferencia(LocalDate.of(2024, 2, 28))).isEqualTo(YearMonth.of(2024, 3));
        assertThat(cartao.calcularReferencia(LocalDate.of(2024, 2, 29))).isEqualTo(YearMonth.of(2024, 4));
        assertThat(cartao.calcularReferencia(LocalDate.of(2025, 2, 28))).isEqualTo(YearMonth.of(2025, 4));
        assertThat(cartao.calcularReferencia(LocalDate.of(2024, 12, 31))).isEqualTo(YearMonth.of(2025, 2));
        cartao.setDiaFechamento(10);
        cartao.setDiaVencimento(31);
        assertThat(new Fatura(YearMonth.of(2024, 2), cartao).getDataVencimento()).isEqualTo(LocalDate.of(2024, 2, 29));
        assertThat(new Fatura(YearMonth.of(2025, 2), cartao).getDataVencimento()).isEqualTo(LocalDate.of(2025, 2, 28));
    }

    @Test
    void atualizarStatusPeloCalendarioEPreservarPagamento() {
        LocalDate hoje = LocalDate.now();
        CartaoCredito cartao = new CartaoCredito("Crédito", null, BigDecimal.TEN, 1, 28);
        Fatura aberta = new Fatura(YearMonth.from(hoje).plusMonths(2), cartao);
        aberta.recalcular();
        assertThat(aberta.getStatus()).isEqualTo(StatusFaturaEnum.ABERTA);

        CartaoCredito fechaHoje = new CartaoCredito("Crédito", null, BigDecimal.TEN, hoje.getDayOfMonth(), hoje.getDayOfMonth());
        Fatura fechada = new Fatura(YearMonth.from(hoje).plusMonths(1), fechaHoje);
        fechada.recalcular();
        assertThat(fechada.getStatus()).isEqualTo(StatusFaturaEnum.FECHADA);

        Fatura vencida = new Fatura(YearMonth.from(hoje).minusMonths(1), cartao);
        vencida.getLancamentos().add(new Lancamento(BigDecimal.TEN, hoje.minusMonths(1), null, vencida));
        vencida.recalcular();
        assertThat(vencida.getStatus()).isEqualTo(StatusFaturaEnum.VENCIDA);
        vencida.pagar();
        vencida.atualizarStatus();
        assertThat(vencida.getStatus()).isEqualTo(StatusFaturaEnum.PAGA);
    }

    @Test
    void recargaDeVoucherNaoPodeSerAplicadaDuasVezesNoMesmoMes() {
        CartaoVoucher voucher = new CartaoVoucher(
            "Benefício", null, 10, BigDecimal.valueOf(500), BigDecimal.ZERO, TipoVoucherEnum.VA_VR);

        voucher.recarregar(YearMonth.of(2026, 10));
        voucher.recarregar(YearMonth.of(2026, 10));

        assertThat(voucher.getSaldoCorrente()).isEqualByComparingTo("500");
        voucher.recarregar(YearMonth.of(2026, 11));
        assertThat(voucher.getSaldoCorrente()).isEqualByComparingTo("1000");
    }
}
