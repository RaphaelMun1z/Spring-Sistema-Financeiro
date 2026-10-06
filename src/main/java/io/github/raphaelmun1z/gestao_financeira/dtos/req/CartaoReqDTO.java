package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;

public record CartaoReqDTO(
    @NotNull(message = "O 'apelido' é obrigatório!") String apelido,
    @NotNull(message = "A 'contaBancariaId' é obrigatória!") String contaBancariaId,
    CartaoPadraoReqDTO cartaoPadrao,
    CartaoVouncherReqDTO cartaoVouncher
) {
}