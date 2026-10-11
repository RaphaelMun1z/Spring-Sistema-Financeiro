package io.github.raphaelmun1z.gestao_financeira.entities;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

@Entity
@Table(name = "tb_faturas")
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;
    private YearMonth dataReferencia;
    private StatusFaturaEnum status;

    @ManyToOne
    @JoinColumn(name = "cartao_credito_id")
    private CartaoCredito cartaoDeCredito;

    @OneToMany(mappedBy = "fatura")
    private Set<Lancamento> lancamentos;

    public Fatura() {
    }

    public Fatura(
        BigDecimal valorTotal,
        YearMonth dataReferencia,
        StatusFaturaEnum status,
        CartaoCredito cartaoDeCredito,
        Set<Lancamento> lancamentos
    ) {
        this.valorTotal = valorTotal;
        this.dataReferencia = dataReferencia;
        this.status = status;
        this.cartaoDeCredito = cartaoDeCredito;
        this.lancamentos = lancamentos;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public YearMonth getDataReferencia() {
        return dataReferencia;
    }

    public StatusFaturaEnum getStatus() {
        return status;
    }

    public CartaoCredito getCartaoDeCredito() {
        return cartaoDeCredito;
    }

    public Set<Lancamento> getLancamentos() {
        return lancamentos;
    }
}
