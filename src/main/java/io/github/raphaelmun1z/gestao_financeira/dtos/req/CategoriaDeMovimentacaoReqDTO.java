package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CategoriaDeMovimentacaoReqDTO(
    @NotBlank(message = "O 'nome' é obrigatório!") String nome,
    @NotNull(message = "O 'tipo' é obrigatório!") TipoCategoriaEnum tipo,
    @NotBlank(message = "A 'cor' é obrigatória!")
    @Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "A 'cor' deve estar no formato hexadecimal. Exemplo: #FF0000"
    )
    String cor,
    @NotBlank(message = "O 'icone' é obrigatório!") String icone
) {
}
