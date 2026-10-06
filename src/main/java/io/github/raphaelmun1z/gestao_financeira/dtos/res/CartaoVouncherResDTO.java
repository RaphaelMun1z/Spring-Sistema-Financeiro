package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVouncher;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoVouncherEnum;

import java.math.BigDecimal;

public record CartaoVouncherResDTO(
    String id,
    String apelido,
    String contaBancariaId,
    Integer diaRecarga,
    BigDecimal valorRecarga,
    BigDecimal saldoCorrente,
    TipoCartaoVouncherEnum tipoCartao
) implements CartaoResDTO {
    public CartaoVouncherResDTO(CartaoVouncher cartaoVouncher) {
        this(
            cartaoVouncher.getId(),
            cartaoVouncher.getApelido(),
            cartaoVouncher.getContaBancariaId(),
            cartaoVouncher.getDiaRecarga(),
            cartaoVouncher.getValorRecarga(),
            cartaoVouncher.getSaldoCorrente(),
            cartaoVouncher.getTipoCartao()
        );
    }
}
