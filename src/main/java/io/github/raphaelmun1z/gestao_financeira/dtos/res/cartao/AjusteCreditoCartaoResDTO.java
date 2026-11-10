package io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.AjusteCreditoCartao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AjusteCreditoCartaoResDTO(
    String id,
    String cartaoId,
    String usuarioId,
    LocalDateTime registradoEm,
    BigDecimal creditoUtilizadoAnterior,
    BigDecimal creditoUtilizadoDesejado,
    BigDecimal creditoDisponivel
) {
    public AjusteCreditoCartaoResDTO(AjusteCreditoCartao ajuste) {
        this(ajuste.getId(), ajuste.getCartaoCredito().getId(), ajuste.getUsuario().getId(), ajuste.getRegistradoEm(),
            ajuste.getCreditoUtilizadoAnterior(), ajuste.getCreditoUtilizadoDesejado(),
            ajuste.getCartaoCredito().getLimiteCredito().subtract(ajuste.getCreditoUtilizadoDesejado()));
    }
}
