package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoVouncherEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Set;


@Entity
@Table(name = "tb_cartoes_vouncher")
public class CartaoVouncher extends Cartao {
    private Integer diaRecarga;
    private BigDecimal valorRecarga;
    private BigDecimal saldoCorrente;
    private TipoCartaoVouncherEnum tipoCartao;

    @OneToMany(mappedBy = "cartao")
    private Set<HistoricoVouncher> historicoDeGastos;

    public CartaoVouncher() {
    }

    public CartaoVouncher(String apelido, ContaBancaria contaBancaria, Integer diaRecarga, BigDecimal valorRecarga, BigDecimal saldoCorrente, TipoCartaoVouncherEnum tipoCartao) {
        super(apelido, contaBancaria);
        this.diaRecarga = diaRecarga;
        this.valorRecarga = valorRecarga;
        this.saldoCorrente = saldoCorrente;
        this.tipoCartao = tipoCartao;
    }

    public Integer getDiaRecarga() {
        return diaRecarga;
    }

    public void setDiaRecarga(Integer diaRecarga) {
        this.diaRecarga = diaRecarga;
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

    public TipoCartaoVouncherEnum getTipoCartao() {
        return tipoCartao;
    }

    public void setTipoCartao(TipoCartaoVouncherEnum tipoCartao) {
        this.tipoCartao = tipoCartao;
    }

    public Set<HistoricoVouncher> getHistoricoDeGastos() {
        return historicoDeGastos;
    }
}
