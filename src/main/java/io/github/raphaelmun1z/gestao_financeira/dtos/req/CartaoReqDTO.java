package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CartaoReqDTO(
    @NotNull(message = "O tipo do cartão é obrigatório!") TipoCartaoEnum tipoCartao,
    @NotBlank(message = "A conta bancária é obrigatória!") String contaBancariaId,
    @NotBlank(message = "O apelido do cartão é obrigatório!") String apelido,
    @Valid CartaoCreditoReqDTO cartaoCredito,
    @Valid CartaoVouncherReqDTO cartaoVoucher
) {
}

