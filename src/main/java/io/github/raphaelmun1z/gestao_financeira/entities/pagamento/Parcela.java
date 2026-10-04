package io.github.raphaelmun1z.gestao_financeira.entities.pagamento;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.RegistroDeDespesas;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;

import java.math.BigDecimal;
import java.util.Date;

public class Parcela {
    private BigDecimal valorParcela;
    private Date dataLancamento;
    private Lancamento lancamento;
    private RegistroDeDespesas registroDeDespesas;

    public Parcela() {
    }

    public Parcela(BigDecimal valorParcela, Date dataLancamento, Lancamento lancamento, RegistroDeDespesas registroDeDespesas) {
        this.valorParcela = valorParcela;
        this.dataLancamento = dataLancamento;
        this.lancamento = lancamento;
        this.registroDeDespesas = registroDeDespesas;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public Lancamento getLancamento() {
        return lancamento;
    }

    public RegistroDeDespesas getRegistroDeDespesas() {
        return registroDeDespesas;
    }
}
