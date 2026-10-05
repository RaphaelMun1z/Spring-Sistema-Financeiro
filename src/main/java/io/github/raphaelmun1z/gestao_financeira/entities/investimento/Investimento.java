package io.github.raphaelmun1z.gestao_financeira.entities.investimento;

import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "tb_investimentos")
public class Investimento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String categoria;
    private Date dataInicio;
    private BigDecimal valorInicial;
    private BigDecimal valorCorrente;
    private Float jurosAoMes;

    @ManyToOne
    @JoinColumn(name = "investidor_id")
    private Usuario investidor;

    public Investimento() {
    }

    public Investimento(String categoria, Date dataInicio, BigDecimal valorInicial, BigDecimal valorCorrente, Float jurosAoMes) {
        this.categoria = categoria;
        this.dataInicio = dataInicio;
        this.valorInicial = valorInicial;
        this.valorCorrente = valorCorrente;
        this.jurosAoMes = jurosAoMes;
    }

    public String getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public BigDecimal getValorCorrente() {
        return valorCorrente;
    }

    public void setValorCorrente(BigDecimal valorCorrente) {
        this.valorCorrente = valorCorrente;
    }

    public Float getJurosAoMes() {
        return jurosAoMes;
    }

    public void setJurosAoMes(Float jurosAoMes) {
        this.jurosAoMes = jurosAoMes;
    }
}
