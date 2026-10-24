package io.github.raphaelmun1z.gestao_financeira;

import io.github.raphaelmun1z.gestao_financeira.entities.Investimento;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AgendamentoDomainTests {
    @Test
    void recorrenciaMensalPreservaODiaOriginalEmMesesCurtos() {
        RegistroDeMovimentacao registro = new RegistroDeMovimentacao(
            BigDecimal.TEN, LocalDate.of(2024, 1, 31), "Conta", MetodoPagamentoEnum.PIX,
            true, 1, null, null, null);

        assertThat(registro.getProximaRecorrencia()).isEqualTo(LocalDate.of(2024, 2, 29));
        registro.avancarRecorrencia();
        assertThat(registro.getProximaRecorrencia()).isEqualTo(LocalDate.of(2024, 3, 31));
    }

    @Test
    void rendimentoCompostoComecaAposOPrimeiroMes() {
        Investimento investimento = new Investimento(
            "Reserva", LocalDate.of(2024, 1, 31), new BigDecimal("100.00"),
            new BigDecimal("100.00"), 10f, null);

        investimento.aplicarRendimentosAte(LocalDate.of(2024, 2, 27));
        assertThat(investimento.getValorCorrente()).isEqualByComparingTo("100.00");

        investimento.aplicarRendimentosAte(LocalDate.of(2024, 2, 29));
        assertThat(investimento.getValorCorrente()).isEqualByComparingTo("110.00");

        investimento.aplicarRendimentosAte(LocalDate.of(2024, 3, 31));
        assertThat(investimento.getValorCorrente()).isEqualByComparingTo("121.00");
    }
}
