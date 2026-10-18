package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;

public record CartaoDebitoResDTO(
    String id,
    String apelido,
    TipoCartaoEnum tipoCartao,
    String contaBancariaId
) implements CartaoResDTO {
    public CartaoDebitoResDTO(CartaoDebito cd) {
        this(
            cd.getId(),
            cd.getApelido(),
            TipoCartaoEnum.DEBITO,
            cd.getContaBancariaId()
        );
    }
}
