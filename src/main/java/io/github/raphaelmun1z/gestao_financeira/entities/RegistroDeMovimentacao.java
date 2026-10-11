package io.github.raphaelmun1z.gestao_financeira.entities;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "tb_registro_movimentacoes")
public class RegistroDeMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;
    private LocalDate dataLancamento;
    private String descricao;
    private MetodoPagamentoEnum metodoPagamento;
    private Boolean ehRecorrente;
    private Integer quantidadeParcelas;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private CategoriaDeMovimentacao categoria;

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id")
    private ContaBancaria contaBancaria;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private Cartao cartao;

    @OneToMany(
        mappedBy = "registroDeMovimentacao",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Lancamento> lancamentos;

    public RegistroDeMovimentacao() {
    }

    public RegistroDeMovimentacao(
        BigDecimal valorTotal,
        LocalDate dataLancamento,
        String descricao,
        MetodoPagamentoEnum metodoPagamento,
        Boolean ehRecorrente,
        Integer quantidadeParcelas,
        CategoriaDeMovimentacao categoria,
        ContaBancaria contaBancaria,
        Cartao cartao,
        Set<Lancamento> lancamentos
    ) {
        this.valorTotal = valorTotal;
        this.dataLancamento = dataLancamento;
        this.descricao = descricao;
        this.metodoPagamento = metodoPagamento;
        this.ehRecorrente = ehRecorrente;
        this.quantidadeParcelas = quantidadeParcelas;
        this.categoria = categoria;
        this.contaBancaria = contaBancaria;
        this.cartao = cartao;
        this.lancamentos = lancamentos;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public MetodoPagamentoEnum getMetodoPagamento() {
        return metodoPagamento;
    }

    public Boolean getEhRecorrente() {
        return ehRecorrente;
    }

    public Integer getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public CategoriaDeMovimentacao getCategoria() {
        return categoria;
    }

    public ContaBancaria getContaBancaria() {
        return contaBancaria;
    }

    public Cartao getCartao() {
        return cartao;
    }

    public Set<Lancamento> getParcelas() {
        return lancamentos;
    }
}
