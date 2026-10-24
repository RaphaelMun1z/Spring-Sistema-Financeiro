package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Set;

@Entity
@Table(name = "tb_cartoes_credito")
public class CartaoCredito extends Cartao {
    private BigDecimal limiteCredito;
    private Integer diaFechamento;
    private Integer diaVencimento;

    @OneToMany(mappedBy = "cartaoDeCredito")
    private Set<Fatura> faturas;

    public CartaoCredito() {
    }

    public CartaoCredito(
        String apelido,
        ContaBancaria contaBancaria,
        BigDecimal limiteCredito,
        Integer diaFechamento,
        Integer diaVencimento
    ) {
        super(apelido, contaBancaria);
        this.limiteCredito = limiteCredito;
        this.diaFechamento = diaFechamento;
        this.diaVencimento = diaVencimento;
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

    public Set<Fatura> getFaturas() {
        return faturas;
    }

    public YearMonth calcularReferencia(LocalDate dataCompra) {
        YearMonth mesFechamento = YearMonth.from(dataCompra);
        LocalDate fechamento = mesFechamento.atDay(Math.min(diaFechamento, mesFechamento.lengthOfMonth()));
        if (!dataCompra.isBefore(fechamento)) {
            mesFechamento = mesFechamento.plusMonths(1);
        }
        return diaVencimento <= diaFechamento ? mesFechamento.plusMonths(1) : mesFechamento;
    }
}
