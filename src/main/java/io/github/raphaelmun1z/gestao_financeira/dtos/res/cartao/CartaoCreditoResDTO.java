package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;

import java.math.BigDecimal;

public record CartaoCreditoResDTO(
    String id,
    String apelido,
    TipoCartaoEnum tipoCartao,
    String contaBancariaId,
    BigDecimal limiteCredito,
    Integer diaFechamento,
    Integer diaVencimento
) implements CartaoResDTO {
    public CartaoCreditoResDTO(CartaoCredito cc) {
        this(
            cc.getId(),
            cc.getApelido(),
            TipoCartaoEnum.CREDITO,
            cc.getContaBancariaId(),
            cc.getLimiteCredito(),
            cc.getDiaFechamento(),
            cc.getDiaVencimento()
        );
    }
}
