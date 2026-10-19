package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "tb_faturas",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"data_referencia", "cartao_credito_id"})
    }
)
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;

    @Column(name = "data_referencia", nullable = false, length = 7)
    private YearMonth dataReferencia;

    @Enumerated(EnumType.STRING)
    private StatusFaturaEnum status;

    @ManyToOne
    @JoinColumn(name = "cartao_credito_id", nullable = false)
    private CartaoCredito cartaoDeCredito;

    @OneToMany(mappedBy = "fatura")
    private final Set<Lancamento> lancamentos = new HashSet<>();

    public Fatura() {
    }

    public Fatura(
        YearMonth dataReferencia,
        CartaoCredito cartaoDeCredito
    ) {
        this.valorTotal = BigDecimal.valueOf(0);
        this.dataReferencia = dataReferencia;
        this.status = StatusFaturaEnum.ABERTA;
        this.cartaoDeCredito = cartaoDeCredito;
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
