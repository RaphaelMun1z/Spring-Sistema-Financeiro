package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoPadraoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "tb_cartoes_padrao")
public class CartaoPadrao extends Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal limiteCredito;
    private Integer diaFechamento;
    private Integer diaVencimento;
    private TipoCartaoPadraoEnum tipoCartao;
    private Set<Fatura> faturas;

    public CartaoPadrao() {
    }

    public CartaoPadrao(String apelido, ContaBancaria contaBancaria, BigDecimal limiteCredito, Integer diaFechamento, Integer diaVencimento, TipoCartaoPadraoEnum tipoCartao) {
        super(apelido, contaBancaria);
        this.limiteCredito = limiteCredito;
        this.diaFechamento = diaFechamento;
        this.diaVencimento = diaVencimento;
        this.tipoCartao = tipoCartao;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public Integer getDiaFechamento() {
        return diaFechamento;
    }

    public void setDiaFechamento(Integer diaFechamento) {
        this.diaFechamento = diaFechamento;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public TipoCartaoPadraoEnum getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartaoPadraoEnum tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public Set<Fatura> getFaturas() {
        return faturas;
    }
}
