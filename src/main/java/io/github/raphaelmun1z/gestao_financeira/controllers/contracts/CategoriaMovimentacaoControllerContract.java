
package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.OrcamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Categorias de Movimentação",
    description = "Gerenciamento de categorias de movimentação financeira e seus orçamentos."
)
@SecurityRequirement(name = "bearerAuth")
public interface CategoriaMovimentacaoControllerContract {

    @Operation(
        summary = "Cadastrar categoria",
        description = "Cadastra uma nova categoria de movimentação financeira."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria cadastrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "409", description = "Conflito ao cadastrar a categoria.")
    })
    ResponseEntity<CategoriaMovimentacaoResDTO> cadastrar(
        @RequestBody @Valid CategoriaDeMovimentacaoReqDTO data
    );

    @Operation(
        summary = "Consultar categoria por ID",
        description = "Retorna os detalhes de uma categoria de movimentação específica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoria consultada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada.")
    })
    ResponseEntity<CategoriaMovimentacaoResDTO> consultarPorId(
        @Parameter(description = "Identificador da categoria.", required = true)
        @PathVariable String categoriaId
    );

    @Operation(
        summary = "Listar categorias",
        description = "Retorna as categorias de movimentação financeira do usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categorias consultadas com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<List<CategoriaMovimentacaoResDTO>> consultarTodos();

    @Operation(
        summary = "Cadastrar orçamento",
        description = "Cadastra um orçamento vinculado à categoria informada."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orçamento cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada."),
        @ApiResponse(responseCode = "409", description = "Já existe um orçamento para essa categoria no mês de referência.")
    })
    ResponseEntity<OrcamentoResDTO> cadastrarOrcamento(
        @Parameter(description = "Identificador da categoria.", required = true)
        @PathVariable String categoriaId,

        @RequestBody @Valid OrcamentoReqDTO data
    );

    @Operation(
        summary = "Listar orçamentos da categoria",
        description = "Retorna os orçamentos cadastrados para a categoria informada."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orçamentos consultados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada.")
    })
    ResponseEntity<List<OrcamentoResDTO>> consultarOrcamentos(
        @Parameter(description = "Identificador da categoria.", required = true)
        @PathVariable String categoriaId
    );
}
