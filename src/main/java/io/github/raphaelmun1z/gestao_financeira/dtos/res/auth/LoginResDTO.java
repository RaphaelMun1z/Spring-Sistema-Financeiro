package io.github.raphaelmun1z.gestao_financeira.dtos.res.auth;

import io.github.raphaelmun1z.gestao_financeira.dtos.TokenDTO;

public record LoginResDTO(
    TokenDTO token
) {
}
