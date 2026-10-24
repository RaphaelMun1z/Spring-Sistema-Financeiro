package io.github.raphaelmun1z.gestao_financeira.entities;

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
@Table(name = "tb_snapshots_investimentos_mensais", uniqueConstraints = @UniqueConstraint(columnNames = {"investimento_id", "mes_referencia"}))
public class SnapshotInvestimentoMensal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "investimento_id", nullable = false)
    private Investimento investimento;

    private YearMonth mesReferencia;
    private LocalDate dataFechamento;
    private BigDecimal valorFinal;

    protected SnapshotInvestimentoMensal() {
    }

    public SnapshotInvestimentoMensal(Investimento investimento, YearMonth mesReferencia,
                                      LocalDate dataFechamento, BigDecimal valorFinal) {
        this.investimento = investimento;
        this.mesReferencia = mesReferencia;
        this.dataFechamento = dataFechamento;
        this.valorFinal = valorFinal;
    }

    public String getId() { return id; }
    public Investimento getInvestimento() { return investimento; }
    public YearMonth getMesReferencia() { return mesReferencia; }
    public LocalDate getDataFechamento() { return dataFechamento; }
    public BigDecimal getValorFinal() { return valorFinal; }
}
