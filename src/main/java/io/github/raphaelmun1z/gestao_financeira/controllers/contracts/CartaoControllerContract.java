package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
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
    name = "Cartões",
    description = "Operações de gerenciamento de cartões e consulta de faturas."
)
@SecurityRequirement(name = "bearerAuth")
public interface CartaoControllerContract {

    @Operation(
        summary = "Cadastrar cartão",
        description = "Cadastra um novo cartão vinculado a uma conta bancária."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cartão cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada."),
        @ApiResponse(responseCode = "409", description = "Conflito ao cadastrar o cartão.")
    })
    ResponseEntity<CartaoResDTO> cadastrar(
        @RequestBody @Valid CartaoReqDTO data
    );

    @Operation(
        summary = "Listar cartões por conta bancária",
        description = "Retorna os cartões cadastrados em uma conta bancária."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cartões consultados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<List<CartaoResDTO>> consultarPorContaBancaria(
        @Parameter(description = "Identificador da conta bancária.", required = true)
        @PathVariable String contaBancariaId
    );

    @Operation(
        summary = "Consultar faturas por período",
        description = "Retorna as faturas de um cartão de crédito dentro do período informado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Faturas consultadas com sucesso."),
        @ApiResponse(responseCode = "400", description = "Período informado inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Cartão não encontrado.")
    })
    ResponseEntity<List<FaturaResDTO>> consultarFaturasPorPeriodo(
        @Parameter(description = "Identificador do cartão.", required = true)
        @PathVariable String cartaoId,

        @RequestBody @Valid ConsultaPorPeriodoReqDTO data
    );
}