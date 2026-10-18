package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoVoucherEnum;

import java.math.BigDecimal;

public record CartaoVoucherResDTO(
    String id,
    String apelido,
    TipoCartaoEnum tipoCartao,
    String contaBancariaId,
    int diaRecarga,
    BigDecimal valorRecarga,
    BigDecimal saldoCorrente,
    TipoVoucherEnum tipoVoucher
) implements CartaoResDTO {
    public CartaoVoucherResDTO(CartaoVoucher cv) {
        this(
            cv.getId(),
            cv.getApelido(),
            TipoCartaoEnum.VOUCHER,
            cv.getContaBancariaId(),
            cv.getDiaRecarga(),
            cv.getValorRecarga(),
            cv.getSaldoCorrente(),
            cv.getTipoCartao()
        );
    }
}
