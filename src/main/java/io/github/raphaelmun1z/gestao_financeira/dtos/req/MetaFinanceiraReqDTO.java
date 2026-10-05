package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MetaFinanceiraReqDTO(
    @NotBlank(message = "O 'objetivo' é obrigatório!") String objetivo,
    @NotNull(message = "O 'valorAlvo' é obrigatório!") BigDecimal valorAlvo,
    BigDecimal valorAcumulado,
    @NotNull(message = "A 'dataConclusaoPrevista' é obrigatória!") YearMonth dataConclusaoPrevista
) {
    public MetaFinanceiraReqDTO {
        if (valorAcumulado == null) {
            valorAcumulado = BigDecimal.ZERO;
        }
    }
}
