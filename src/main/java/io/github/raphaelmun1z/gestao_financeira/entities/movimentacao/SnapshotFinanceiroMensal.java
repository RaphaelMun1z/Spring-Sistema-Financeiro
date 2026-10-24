package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "tb_snapshots_financeiros_mensais", uniqueConstraints = @UniqueConstraint(columnNames = {"conta_bancaria_id", "mes_referencia"}))
public class SnapshotFinanceiroMensal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id", nullable = false)
    private ContaBancaria contaBancaria;

    private YearMonth mesReferencia;
    private LocalDate dataFechamento;
    private BigDecimal saldoFinal;
    private BigDecimal creditoTotal;
    private BigDecimal creditoRestante;
    private BigDecimal totalEntradas;
    private BigDecimal totalSaidas;

    protected SnapshotFinanceiroMensal() {
    }

    public SnapshotFinanceiroMensal(ContaBancaria contaBancaria, YearMonth mesReferencia, LocalDate dataFechamento,
                                    BigDecimal saldoFinal, BigDecimal creditoTotal, BigDecimal creditoRestante,
                                    BigDecimal totalEntradas, BigDecimal totalSaidas) {
        this.contaBancaria = contaBancaria;
        this.mesReferencia = mesReferencia;
        this.dataFechamento = dataFechamento;
        this.saldoFinal = saldoFinal;
        this.creditoTotal = creditoTotal;
        this.creditoRestante = creditoRestante;
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
    }

    public String getId() { return id; }
    public ContaBancaria getContaBancaria() { return contaBancaria; }
    public YearMonth getMesReferencia() { return mesReferencia; }
    public LocalDate getDataFechamento() { return dataFechamento; }
    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public BigDecimal getCreditoTotal() { return creditoTotal; }
    public BigDecimal getCreditoRestante() { return creditoRestante; }
    public BigDecimal getTotalEntradas() { return totalEntradas; }
    public BigDecimal getTotalSaidas() { return totalSaidas; }
}
