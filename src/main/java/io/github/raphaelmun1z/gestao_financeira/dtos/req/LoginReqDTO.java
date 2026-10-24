package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

public record LoginReqDTO(
    @NotBlank(message = "O 'email' é obrigatório!") @Email String email,
    @NotBlank(message = "A 'senha' é obrigatória!")String senha
) {
}
