package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MetaFinanceiraReqDTO(
    @NotBlank(message = "O 'objetivo' é obrigatório!") @Size(max = 255) String objetivo,
    @NotNull(message = "O 'valorAlvo' é obrigatório!") @Positive @Digits(integer = 17, fraction = 2) BigDecimal valorAlvo,
    @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal valorAcumulado,
    @NotNull(message = "A 'dataConclusaoPrevista' é obrigatória!") @FutureOrPresent YearMonth dataConclusaoPrevista
) {
    public MetaFinanceiraReqDTO {
        if (valorAcumulado == null) {
            valorAcumulado = BigDecimal.ZERO;
        }
    }
}
