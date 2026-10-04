package io.github.raphaelmun1z.gestao_financeira.entities.conta;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;

import java.math.BigDecimal;
import java.util.Set;

public class ContaBancaria {
    private String nomeDoBanco;
    private BigDecimal saldoCorrente;
    private BigDecimal creditoTotal;
    private BigDecimal creditoRestante;
    private Usuario titular;
    private Set<Cartao> cartoes;

    public ContaBancaria() {
    }

    public ContaBancaria(String nomeDoBanco, BigDecimal saldoCorrente, BigDecimal creditoTotal, BigDecimal creditoRestante) {
        this.nomeDoBanco = nomeDoBanco;
        this.saldoCorrente = saldoCorrente;
        this.creditoTotal = creditoTotal;
        this.creditoRestante = creditoRestante;
    }

    public String getNomeDoBanco() {
        return nomeDoBanco;
    }

    public void setNomeDoBanco(String nomeDoBanco) {
        this.nomeDoBanco = nomeDoBanco;
    }

    public BigDecimal getSaldoCorrente() {
        return saldoCorrente;
    }

    public BigDecimal getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(BigDecimal creditoTotal) {
        this.creditoTotal = creditoTotal;
    }

    public BigDecimal getCreditoRestante() {
        return creditoRestante;
    }

    public Usuario getTitular() {
        return titular;
    }

    public Set<Cartao> getCartoes() {
        return cartoes;
    }
}
