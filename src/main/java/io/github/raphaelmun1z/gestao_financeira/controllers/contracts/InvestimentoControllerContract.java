
package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.InvestimentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
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
    name = "Investimentos",
    description = "Gerenciamento e consulta de investimentos financeiros."
)
@SecurityRequirement(name = "bearerAuth")
public interface InvestimentoControllerContract {

    @Operation(
        summary = "Cadastrar investimento",
        description = "Cadastra um novo investimento vinculado ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Investimento cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "409", description = "Já existe um investimento com essa categoria para o usuário.")
    })
    ResponseEntity<InvestimentoResDTO> cadastrar(
        @RequestBody @Valid InvestimentoReqDTO data
    );

    @Operation(
        summary = "Listar investimentos",
        description = "Retorna os investimentos cadastrados pelo usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Investimentos consultados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<List<InvestimentoResDTO>> consultarTodos();
}
