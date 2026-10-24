package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotNull;

public record RecorrenciaReqDTO(
    @NotNull Boolean ativa
) {
}
