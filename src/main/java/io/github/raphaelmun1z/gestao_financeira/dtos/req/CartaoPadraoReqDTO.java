package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoPadraoEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartaoPadraoReqDTO(
    @NotNull(message = "O 'limiteCredito' é obrigatório!") BigDecimal limiteCredito,
    @NotNull(message = "O 'diaFechamento' é obrigatório!") Integer diaFechamento,
    @NotNull(message = "O 'diaVencimento' é obrigatório!") Integer diaVencimento,
    @NotNull(message = "O 'tipoCartao' é obrigatório!") TipoCartaoPadraoEnum tipoCartao
) {
}
