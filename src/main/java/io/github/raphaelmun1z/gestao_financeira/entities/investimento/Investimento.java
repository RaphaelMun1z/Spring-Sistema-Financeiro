package io.github.raphaelmun1z.gestao_financeira.entities.investimento;

import java.math.BigDecimal;
import java.util.Date;

public class Investimento {
    private String categoria;
    private Date dataInicio;
    private BigDecimal valorInicial;
    private BigDecimal valorCorrente;
    private Float jurosAoMes;

    public Investimento() {
    }

    public Investimento(String categoria, Date dataInicio, BigDecimal valorInicial, BigDecimal valorCorrente, Float jurosAoMes) {
        this.categoria = categoria;
        this.dataInicio = dataInicio;
        this.valorInicial = valorInicial;
        this.valorCorrente = valorCorrente;
        this.jurosAoMes = jurosAoMes;
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
