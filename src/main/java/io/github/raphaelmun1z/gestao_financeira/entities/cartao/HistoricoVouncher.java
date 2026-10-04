package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity
@Table(name = "tb_historicos_vouncher")
public class HistoricoVouncher extends RegistroDeDespesas {
    public HistoricoVouncher() {
    }

    public HistoricoVouncher(BigDecimal valorTotal, YearMonth dataReferencia) {
        super(valorTotal, dataReferencia);
    }
}
