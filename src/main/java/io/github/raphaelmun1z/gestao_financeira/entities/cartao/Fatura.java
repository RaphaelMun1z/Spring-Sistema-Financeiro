package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity
@Table(name = "tb_faturas")
public class Fatura extends RegistroDeDespesas {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Boolean foiPago;

    public Fatura() {
    }

    public Fatura(BigDecimal valorTotal, YearMonth dataReferencia) {
        super(valorTotal, dataReferencia);
    }

    public Boolean getFoiPago() {
        return foiPago;
    }

    public void setFoiPago(Boolean foiPago) {
        this.foiPago = foiPago;
    }
}
