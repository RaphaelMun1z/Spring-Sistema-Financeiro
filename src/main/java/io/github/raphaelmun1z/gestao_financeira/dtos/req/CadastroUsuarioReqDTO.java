package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastroUsuarioReqDTO(
    @NotBlank(message = "O 'nome' é obrigatório!")
    @Size(max = 255, message = "O 'nome' deve ter no máximo 255 caracteres.")
    String nome,

    @NotBlank(message = "O 'email' é obrigatório!")
    @Email(message = "O 'email' deve ser válido.")
    @Size(max = 255, message = "O 'email' deve ter no máximo 255 caracteres.")
    String email,

    @NotBlank(message = "A 'senha' é obrigatória!")
    @Size(min = 12, max = 72, message = "A 'senha' deve ter entre 12 e 72 caracteres.")
    String senha
) {
}
