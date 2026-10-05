package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ContaBancariaReqDTO(
    @NotBlank(message = "O 'nomeDoBanco' é obrigatório!") String nomeDoBanco,
    @Min(value = 0) BigDecimal saldoCorrente,
    @NotNull(message = "O 'creditoTotal' é obrigatório!")
    @Min(value = 0)
    BigDecimal creditoTotal,
    @Min(value = 0) BigDecimal creditoRestante
) {
    public ContaBancariaReqDTO {
        if (saldoCorrente == null) {
            saldoCorrente = BigDecimal.valueOf(0.0);
        }

        if (creditoRestante == null) {
            creditoRestante = creditoTotal;
        }
    }
}
