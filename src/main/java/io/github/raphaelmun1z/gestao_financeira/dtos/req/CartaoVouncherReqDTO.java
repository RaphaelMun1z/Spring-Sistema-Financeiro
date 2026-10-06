package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoVouncherEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CartaoVouncherReqDTO(
    @NotNull(message = "O 'diaRecarga' é obrigatório!") Integer diaRecarga,
    @NotNull(message = "O 'valorRecarga' é obrigatório!") BigDecimal valorRecarga,
    BigDecimal saldoCorrente,
    @NotNull(message = "O 'tipoCartao' é obrigatório!") TipoCartaoVouncherEnum tipoCartao
) {
}
