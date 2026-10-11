package io.github.raphaelmun1z.gestao_financeira.entities;

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
        this.valor = valor;
        this.dataLancamento = dataLancamento;
        this.registroDeMovimentacao = registroDeMovimentacao;
        this.fatura = fatura;
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

    public RegistroDeMovimentacao getRegistroDeMovimentacao() {
        return registroDeMovimentacao;
    }

    public Fatura getFatura() {
        return fatura;
    }
}