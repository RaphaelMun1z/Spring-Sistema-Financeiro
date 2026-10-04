package io.github.raphaelmun1z.gestao_financeira.entities.orcamento;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity
@Table(name = "tb_orcamentos")
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private YearMonth mesReferencia;
    private BigDecimal valorLimite;
    private BigDecimal valorCorrente;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaDeMovimentacao categoria;

    public Orcamento() {
    }

    public Orcamento(YearMonth mesReferencia, BigDecimal valorLimite, CategoriaDeMovimentacao categoria) {
        this.mesReferencia = mesReferencia;
        this.valorLimite = valorLimite;
        this.categoria = categoria;
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

    public CategoriaDeMovimentacao getCategoria() {
        return categoria;
    }
}
