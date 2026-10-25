package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.TelegramVinculoResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Telegram", description = "Código persistente para vincular a conta ao bot do Telegram.")
public interface TelegramControllerContract {

    @Operation(
        summary = "Consultar código de vínculo do Telegram",
        description = "Retorna o código atual do usuário autenticado, criando um caso ainda não exista. O código não expira.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Código de vínculo retornado."),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado.")
    })
    ResponseEntity<TelegramVinculoResDTO> obterCodigo();

    @Operation(
        summary = "Gerar novo código de vínculo do Telegram",
        description = "Substitui o código atual do usuário autenticado. O código anterior deixa de ser válido para novos vínculos; o vínculo Telegram já existente permanece ativo.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Novo código de vínculo gerado."),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado.")
    })
    ResponseEntity<TelegramVinculoResDTO> gerarNovoCodigo();
}
