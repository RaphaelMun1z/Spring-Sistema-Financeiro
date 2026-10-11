package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

public sealed interface CartaoResDTO permits CartaoCreditoResDTO, CartaoDebitoResDTO, CartaoVoucherResDTO {
    String apelido();

    String contaBancariaId();
}