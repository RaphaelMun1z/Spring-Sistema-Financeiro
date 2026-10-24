package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvestimentoReqDTO(
    @NotBlank(message = "A 'categoria' é obrigatória!") @Size(max = 255) String categoria,
    @Schema(description = "Data de início do investimento; o primeiro rendimento ocorre após um mês.") @PastOrPresent LocalDate dataInicio,
    @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal valorInicial,
    @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal valorCorrente,
    @Schema(description = "Taxa mensal em percentual. Exemplo: 1 representa 1% ao mês, com capitalização composta.") @NotNull(message = "O 'jurosAoMes' é obrigatório!") @PositiveOrZero Float jurosAoMes
) {
    public InvestimentoReqDTO {
        if (dataInicio == null) {
            dataInicio = LocalDate.now();
        }

        if (valorInicial == null) {
            valorInicial = BigDecimal.ZERO;
        }

        if (valorCorrente == null) {
            valorCorrente = BigDecimal.ZERO;
        }
    }
}
