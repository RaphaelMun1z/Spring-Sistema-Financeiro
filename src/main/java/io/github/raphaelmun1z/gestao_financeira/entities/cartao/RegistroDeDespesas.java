package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

public abstract class RegistroDeDespesas {
    private BigDecimal valorTotal;
    private YearMonth dataReferencia;
    private Set<Parcela> despesas;

    public RegistroDeDespesas() {
    }

    public RegistroDeDespesas(BigDecimal valorTotal, YearMonth dataReferencia) {
        this.valorTotal = valorTotal;
        this.dataReferencia = dataReferencia;
    }
}
