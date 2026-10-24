package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_pagamentos_faturas", uniqueConstraints = @UniqueConstraint(columnNames = "fatura_id"))
public class PagamentoFatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(name = "fatura_id", nullable = false)
    private Fatura fatura;

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    private ContaBancaria contaBancaria;

    private BigDecimal valor;
    private LocalDate dataPagamento;

    protected PagamentoFatura() {
    }

    public PagamentoFatura(Fatura fatura, ContaBancaria contaBancaria, BigDecimal valor, LocalDate dataPagamento) {
        this.fatura = fatura;
        this.contaBancaria = contaBancaria;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        fatura.registrarPagamento(this);
    }

    public String getId() {
        return id;
    }

    public Fatura getFatura() {
        return fatura;
    }

    public ContaBancaria getContaBancaria() {
        return contaBancaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }
}
