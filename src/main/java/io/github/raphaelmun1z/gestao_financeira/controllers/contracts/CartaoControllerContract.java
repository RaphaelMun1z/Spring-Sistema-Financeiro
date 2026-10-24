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
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Cartões",
    description = "Operações de gerenciamento de cartões e consulta de faturas."
)
@SecurityRequirement(name = "bearerAuth")
public interface CartaoControllerContract {

    @Operation(summary = "Editar cartão", description = "Edita o apelido e as configurações do cartão; calendários com faturas não podem ser alterados.")
    ResponseEntity<CartaoResDTO> atualizar(@PathVariable String cartaoId, @RequestBody @Valid CartaoReqDTO data);

    @Operation(summary = "Excluir cartão", description = "Exclui somente cartões sem faturas ou movimentações vinculadas.")
    ResponseEntity<Void> excluir(@PathVariable String cartaoId);

    @Operation(summary = "Pagar fatura",
        description = "Debita integralmente o saldo da conta do cartão e libera o limite. Repetir o pagamento de uma fatura paga não altera os saldos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Fatura paga."),
        @ApiResponse(responseCode = "400", description = "Saldo insuficiente ou fatura sem valor."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Fatura não encontrada.")
    })
    ResponseEntity<FaturaResDTO> pagarFatura(
        @Parameter(description = "Identificador da fatura.", required = true)
        @PathVariable String faturaId
    );

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

}
