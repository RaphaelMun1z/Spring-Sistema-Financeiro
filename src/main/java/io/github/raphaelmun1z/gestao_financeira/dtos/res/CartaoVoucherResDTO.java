package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;

import java.math.BigDecimal;

public record CartaoVoucherResDTO(
    String id,
    String apelido,
    String contaBancariaId,
    int diaRecarga,
    BigDecimal valorRecarga,
    BigDecimal saldoCorrente,
    TipoVoucherEnum tipoCartao
) implements CartaoResDTO {
    public CartaoVoucherResDTO(CartaoVoucher cv) {
        this(
            cv.getId(),
            cv.getApelido(),
            cv.getContaBancariaId(),
            cv.getDiaRecarga(),
            cv.getValorRecarga(),
            cv.getSaldoCorrente(),
            cv.getTipoCartao()
        );
    }
}
