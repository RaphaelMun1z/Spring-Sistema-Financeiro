package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;

import java.math.BigDecimal;

public record CartaoCreditoResDTO(
    String id,
    String apelido,
    String contaBancariaId,
    BigDecimal limiteCredito,
    Integer diaFechamento,
    Integer diaVencimento
) implements CartaoResDTO {
    public CartaoCreditoResDTO(CartaoCredito cc) {
        this(
            cc.getId(),
            cc.getApelido(),
            cc.getContaBancariaId(),
            cc.getLimiteCredito(),
            cc.getDiaFechamento(),
            cc.getDiaVencimento()
        );
    }
}
