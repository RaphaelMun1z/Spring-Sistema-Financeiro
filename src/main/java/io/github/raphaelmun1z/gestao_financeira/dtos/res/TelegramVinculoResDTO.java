package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record TelegramVinculoResDTO(
    @Schema(description = "Código permanente usado pelo bot para vincular a conta do Telegram.", example = "a1b2c3... (64 caracteres hexadecimais)")
    String codigo
) {
}
