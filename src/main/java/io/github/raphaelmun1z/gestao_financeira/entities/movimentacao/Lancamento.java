package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_lancamentos")
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valor;
    private LocalDate dataLancamento;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @ManyToOne
    @JoinColumn(name = "registro_de_movimentacao_id")
    private RegistroDeMovimentacao registroDeMovimentacao;

    @ManyToOne
    @JoinColumn(name = "fatura_id")
    private Fatura fatura;

    public Lancamento() {
    }

    public Lancamento(
        BigDecimal valor,
        LocalDate dataLancamento,
        RegistroDeMovimentacao registroDeMovimentacao,
        Fatura fatura
    ) {
        this(valor, dataLancamento, registroDeMovimentacao, fatura, 1);
    }

    public Lancamento(
        BigDecimal valor,
        LocalDate dataLancamento,
        RegistroDeMovimentacao registroDeMovimentacao,
        Fatura fatura,
        Integer numeroParcela
    ) {
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.registroDeMovimentacao = registroDeMovimentacao;
        this.fatura = fatura;
        this.numeroParcela = numeroParcela;
    }

    public Lancamento(
        BigDecimal valor,
        LocalDate dataLancamento,
        RegistroDeMovimentacao registroDeMovimentacao
    ) {
        this(valor, dataLancamento, registroDeMovimentacao, null, 1);
    }

    public Lancamento(
        BigDecimal valor,
        LocalDate dataLancamento,
        RegistroDeMovimentacao registroDeMovimentacao,
        Integer numeroParcela
    ) {
        this(valor, dataLancamento, registroDeMovimentacao, null, numeroParcela);
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public RegistroDeMovimentacao getRegistroDeMovimentacao() {
        return registroDeMovimentacao;
    }

    public Fatura getFatura() {
        return fatura;
    }
}
