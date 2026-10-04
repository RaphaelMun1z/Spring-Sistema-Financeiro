package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import java.math.BigDecimal;
import java.time.YearMonth;

public class HistoricoVouncher extends RegistroDeDespesas {
    public HistoricoVouncher() {
    }

    public HistoricoVouncher(BigDecimal valorTotal, YearMonth dataReferencia) {
        super(valorTotal, dataReferencia);
    }
}
