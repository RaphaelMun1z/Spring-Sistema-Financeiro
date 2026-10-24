package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Size;

public record MovimentacaoAtualizacaoReqDTO(
    @Size(max = 255) String descricao
) {
}
