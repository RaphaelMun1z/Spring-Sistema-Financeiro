package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartaoVouncherReqDTO(
    @NotNull(message = "O tipo de voucher é obrigatório!") TipoVoucherEnum tipoVoucher,
    @NotNull(message = "O valor da recarga é obrigatório!") BigDecimal valorRecarga,
    @NotNull(message = "O dia da recarga é obrigatório!") int diaRecarga,
    BigDecimal saldoCorrente
) {
}
