
package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.MetaFinanceiraReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Metas Financeiras",
    description = "Gerenciamento e consulta de metas financeiras."
)
@SecurityRequirement(name = "bearerAuth")
public interface MetaFinanceiraControllerContract {

    @Operation(
        summary = "Cadastrar meta financeira",
        description = "Cadastra uma nova meta financeira vinculada ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Meta financeira cadastrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "409", description = "Já existe uma meta financeira com esse objetivo para o usuário.")
    })
    ResponseEntity<MetaFinanceiraResDTO> cadastrar(
        @RequestBody @Valid MetaFinanceiraReqDTO data
    );

    @Operation(
        summary = "Listar metas financeiras",
        description = "Retorna as metas financeiras cadastradas pelo usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Metas financeiras consultadas com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<List<MetaFinanceiraResDTO>> consultarTodos();
}
