package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoPadrao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoPadraoEnum;

import java.math.BigDecimal;

public record CartaoPadraoResDTO(
    String id,
    String apelido,
    String contaBancariaId,
    BigDecimal limiteCredito,
    Integer diaFechamento,
    Integer diaVencimento,
    TipoCartaoPadraoEnum tipoCartao
) implements CartaoResDTO {
    public CartaoPadraoResDTO(CartaoPadrao cartaoPadrao) {
        this(
            cartaoPadrao.getId(),
            cartaoPadrao.getApelido(),
            cartaoPadrao.getContaBancariaId(),
            cartaoPadrao.getLimiteCredito(),
            cartaoPadrao.getDiaFechamento(),
            cartaoPadrao.getDiaVencimento(),
            cartaoPadrao.getTipoCartao()
        );
    }
}
