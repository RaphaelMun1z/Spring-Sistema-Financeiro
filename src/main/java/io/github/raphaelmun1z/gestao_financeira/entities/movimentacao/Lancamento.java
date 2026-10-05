package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;


@Entity
@Table(name = "tb_lancamentos")
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;
    private Date dataLancamento;
    private String descricao;
    private MetodoPagamentoEnum metodoPagamento;
    private Boolean ehRecorrente;

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
        mappedBy = "lancamento",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Parcela> parcelas;

    public Lancamento() {
    }

    public Lancamento(Cartao cartao, ContaBancaria contaBancaria, CategoriaDeMovimentacao categoria, Boolean ehRecorrente, MetodoPagamentoEnum metodoPagamento, String descricao, Date dataLancamento, BigDecimal valorTotal) {
        this.cartao = cartao;
        this.contaBancaria = contaBancaria;
        this.categoria = categoria;
        this.ehRecorrente = ehRecorrente;
        this.metodoPagamento = metodoPagamento;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
        this.valorTotal = valorTotal;
    }

    public Lancamento(ContaBancaria contaBancaria, CategoriaDeMovimentacao categoria, Boolean ehRecorrente, MetodoPagamentoEnum metodoPagamento, String descricao, Date dataLancamento, BigDecimal valorTotal) {
        this.contaBancaria = contaBancaria;
        this.categoria = categoria;
        this.ehRecorrente = ehRecorrente;
        this.metodoPagamento = metodoPagamento;
        this.descricao = descricao;
        this.dataLancamento = dataLancamento;
        this.valorTotal = valorTotal;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public MetodoPagamentoEnum getMetodoPagamento() {
        return metodoPagamento;
    }

    public Boolean getEhRecorrente() {
        return ehRecorrente;
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

    public void setCartao(Cartao cartao) {
        this.cartao = cartao;
    }

    public Set<Parcela> getParcelas() {
        return parcelas;
    }
}
