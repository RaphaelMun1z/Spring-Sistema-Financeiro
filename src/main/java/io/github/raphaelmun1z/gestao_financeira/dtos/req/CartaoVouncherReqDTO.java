package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public record CartaoVouncherReqDTO(
    @NotNull(message = "O tipo de voucher é obrigatório!") TipoVoucherEnum tipoVoucher,
    @NotNull(message = "O valor da recarga é obrigatório!") @Positive @Digits(integer = 17, fraction = 2) BigDecimal valorRecarga,
    @NotNull(message = "O dia da recarga é obrigatório!") @Min(1) @Max(31) Integer diaRecarga,
    @PositiveOrZero @Digits(integer = 17, fraction = 2) BigDecimal saldoCorrente
) {
    public CartaoVouncherReqDTO {
        if (saldoCorrente == null) {
            saldoCorrente = BigDecimal.ZERO;
        }
    }
}
