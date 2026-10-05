package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;

public record LoginReqDTO(
    @NotBlank(message = "O 'email' é obrigatório!") String email,
    @NotBlank(message = "A 'senha' é obrigatória!")String senha
) {
}
