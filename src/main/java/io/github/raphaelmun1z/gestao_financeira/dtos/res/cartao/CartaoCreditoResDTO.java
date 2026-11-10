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
    BigDecimal creditoUsado,
    BigDecimal creditoRestante,
    Integer diaFechamento,
    Integer diaVencimento
) implements CartaoResDTO {
    public CartaoCreditoResDTO(CartaoCredito cc) {
        this(cc, BigDecimal.ZERO);
    }

    public CartaoCreditoResDTO(CartaoCredito cc, BigDecimal creditoUsado) {
        this(
            cc.getId(),
            cc.getApelido(),
            TipoCartaoEnum.CREDITO,
            cc.getContaBancariaId(),
            cc.getLimiteCredito(),
            creditoUsado,
            cc.getLimiteCredito().subtract(creditoUsado),
            cc.getDiaFechamento(),
            cc.getDiaVencimento()
        );
    }
}
