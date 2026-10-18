package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;

public sealed interface CartaoResDTO permits CartaoCreditoResDTO, CartaoDebitoResDTO, CartaoVoucherResDTO {
    String apelido();
    TipoCartaoEnum tipoCartao();

    String contaBancariaId();
}