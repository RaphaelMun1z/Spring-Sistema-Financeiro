package io.github.raphaelmun1z.gestao_financeira.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity
@Table(
    name = "tb_orcamentos",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mes_referencia", "categoria_id"})
    }
)
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "mes_referencia", nullable = false)
    private YearMonth mesReferencia;

    private BigDecimal valorLimite;
    private BigDecimal valorCorrente;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaDeMovimentacao categoria;

    public Orcamento() {
    }

    public Orcamento(YearMonth mesReferencia, BigDecimal valorLimite, BigDecimal valorCorrente, CategoriaDeMovimentacao categoria) {
        this.mesReferencia = mesReferencia;
        this.valorLimite = valorLimite;
        this.valorCorrente = valorCorrente;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public YearMonth getMesReferencia() {
        return mesReferencia;
    }

    public BigDecimal getValorLimite() {
        return valorLimite;
    }

    public void setValorLimite(BigDecimal valorLimite) {
        this.valorLimite = valorLimite;
    }

    public BigDecimal getValorCorrente() {
        return valorCorrente;
    }

    public void setValorCorrente(BigDecimal valorCorrente) {
        this.valorCorrente = valorCorrente;
    }

    public CategoriaDeMovimentacao getCategoria() {
        return categoria;
    }
}
