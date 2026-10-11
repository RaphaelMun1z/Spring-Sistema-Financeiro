package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_cartoes_vouncher")
public class CartaoVoucher extends Cartao {
    private Integer diaRecarga;
    private BigDecimal valorRecarga;
    private BigDecimal saldoCorrente;
    private TipoVoucherEnum tipoCartao;

    public CartaoVoucher() {
    }

    public CartaoVoucher(
        String apelido,
        ContaBancaria contaBancaria,
        Integer diaRecarga,
        BigDecimal valorRecarga,
        BigDecimal saldoCorrente
    ) {
        super(apelido, contaBancaria);
        this.diaRecarga = diaRecarga;
        this.valorRecarga = valorRecarga;
        this.saldoCorrente = saldoCorrente;
    }

    public Integer getDiaRecarga() {
        return diaRecarga;
    }

    public BigDecimal getValorRecarga() {
        return valorRecarga;
    }

    public BigDecimal getSaldoCorrente() {
        return saldoCorrente;
    }

    public TipoVoucherEnum getTipoCartao() {
        return tipoCartao;
    }
}
