package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "tb_cartoes_vouncher")
public class CartaoVoucher extends Cartao {
    private Integer diaRecarga;
    private BigDecimal valorRecarga;
    private BigDecimal saldoCorrente;
    private TipoVoucherEnum tipoCartao;
    @Column(name = "ultima_recarga", length = 7)
    private YearMonth ultimaRecarga;

    public CartaoVoucher() {
    }

    public CartaoVoucher(
        String apelido,
        ContaBancaria contaBancaria,
        Integer diaRecarga,
        BigDecimal valorRecarga,
        BigDecimal saldoCorrente,
        TipoVoucherEnum tipoCartao
    ) {
        super(apelido, contaBancaria);
        this.diaRecarga = diaRecarga;
        this.valorRecarga = valorRecarga;
        this.saldoCorrente = saldoCorrente;
        this.tipoCartao = tipoCartao;
        this.ultimaRecarga = saldoCorrente.signum() > 0 ? YearMonth.now() : null;
    }

    public Integer getDiaRecarga() {
        return diaRecarga;
    }

    public BigDecimal getValorRecarga() {
        return valorRecarga;
    }

    public void setValorRecarga(BigDecimal valorRecarga) {
        this.valorRecarga = valorRecarga;
    }

    public BigDecimal getSaldoCorrente() {
        return saldoCorrente;
    }

    public void setSaldoCorrente(BigDecimal saldoCorrente) {
        this.saldoCorrente = saldoCorrente;
    }

    public TipoVoucherEnum getTipoCartao() {
        return tipoCartao;
    }

    public void setDiaRecarga(Integer diaRecarga) {
        this.diaRecarga = diaRecarga;
    }

    public void setTipoCartao(TipoVoucherEnum tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public YearMonth getUltimaRecarga() {
        return ultimaRecarga;
    }

    public void recarregar(YearMonth mes) {
        if (mes.equals(ultimaRecarga)) {
            return;
        }
        saldoCorrente = saldoCorrente.add(valorRecarga);
        ultimaRecarga = mes;
    }
}
