package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AjusteCreditoCartaoReqDTO(
    @NotNull(message = "O crédito utilizado desejado é obrigatório.")
    @PositiveOrZero(message = "O crédito utilizado desejado não pode ser negativo.")
    @Digits(integer = 17, fraction = 2, message = "O crédito utilizado desejado deve ter no máximo duas casas decimais.")
    BigDecimal creditoUtilizadoDesejado
) {}
