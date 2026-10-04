package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import java.math.BigDecimal;
import java.time.YearMonth;

public class Fatura extends RegistroDeDespesas {
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
