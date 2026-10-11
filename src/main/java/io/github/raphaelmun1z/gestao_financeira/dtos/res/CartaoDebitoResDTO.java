package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;

public record CartaoDebitoResDTO(
    String id,
    String apelido,
    String contaBancariaId
) implements CartaoResDTO {
    public CartaoDebitoResDTO(CartaoDebito cd) {
        this(
            cd.getId(),
            cd.getApelido(),
            cd.getContaBancariaId()
        );
    }
}
