package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.LoginResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.UserDetailsResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Autenticação", description = "Operações de autenticação e identificação do usuário.")
public interface AuthControllerContract {

    @Operation(
        summary = "Autenticar usuário",
        description = "Autentica o usuário utilizando suas credenciais e retorna um token de acesso.",
        security = {}
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Autenticação realizada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas.")
    })
    ResponseEntity<LoginResDTO> acessar(@RequestBody @Valid LoginReqDTO data);

    @Operation(
        summary = "Consultar usuário autenticado",
        description = "Retorna as informações do usuário associado ao token de acesso.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário consultado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado.")
    })
    ResponseEntity<UserDetailsResDTO> me();
}