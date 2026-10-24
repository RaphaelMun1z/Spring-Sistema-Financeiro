package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ContaBancariaReqDTO(
    @NotBlank(message = "O 'nomeDoBanco' é obrigatório!") @Size(max = 255) String nomeDoBanco,
    @Min(value = 0) @Digits(integer = 17, fraction = 2) BigDecimal saldoCorrente,
    @NotNull(message = "O 'creditoTotal' é obrigatório!")
    @Min(value = 0) @Digits(integer = 17, fraction = 2)
    BigDecimal creditoTotal,
    @Min(value = 0) @Digits(integer = 17, fraction = 2) BigDecimal creditoRestante
) {
    public ContaBancariaReqDTO {
        if (saldoCorrente == null) {
            saldoCorrente = BigDecimal.ZERO;
        }

        if (creditoRestante == null) {
            creditoRestante = creditoTotal;
        }
    }

    @AssertTrue(message = "O crédito restante não pode superar o crédito total.")
    public boolean isCreditoValido() {
        return creditoTotal == null || creditoRestante == null || creditoRestante.compareTo(creditoTotal) <= 0;
    }
}
