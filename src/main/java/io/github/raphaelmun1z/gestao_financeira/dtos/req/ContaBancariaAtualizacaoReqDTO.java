package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContaBancariaAtualizacaoReqDTO(
    @NotBlank @Size(max = 255) String nomeDoBanco
) {
}
