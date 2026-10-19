
package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(
    name = "Contas Bancárias",
    description = "Gerenciamento de contas bancárias, registros de movimentação e lançamentos."
)
@SecurityRequirement(name = "bearerAuth")
public interface ContaBancariaControllerContract {

    @Operation(
        summary = "Cadastrar conta bancária",
        description = "Cadastra uma nova conta bancária vinculada ao usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Conta bancária cadastrada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "409", description = "Já existe uma conta com esse nome de banco para o usuário.")
    })
    ResponseEntity<ContaBancariaResDTO> cadastrar(
        @RequestBody @Valid ContaBancariaReqDTO data
    );

    @Operation(
        summary = "Listar contas bancárias",
        description = "Retorna as contas bancárias cadastradas pelo usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contas bancárias consultadas com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<List<ContaBancariaResDTO>> consultarTodos();

    @Operation(
        summary = "Cadastrar registro de movimentação",
        description = "Cadastra um registro de movimentação financeira vinculado à conta bancária informada."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registro cadastrado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada."),
        @ApiResponse(responseCode = "409", description = "Conflito ao cadastrar o registro.")
    })
    ResponseEntity<RegistroMovimentacaoResDTO> cadastrarMovimentacao(
        @Parameter(description = "Identificador da conta bancária.", required = true)
        @PathVariable String contaBancariaId,

        @RequestBody @Valid RegistroMovimentacaoReqDTO data
    );

    @Operation(
        summary = "Consultar registros de movimentação por período",
        description = "Retorna os registros de movimentação de uma conta bancária dentro do período informado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registros consultados com sucesso."),
        @ApiResponse(responseCode = "400", description = "Período informado inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    ResponseEntity<List<RegistroMovimentacaoResDTO>> consultarMovimentacoesPorContaBancariaEPeriodo(
        @Parameter(description = "Identificador da conta bancária.", required = true)
        @PathVariable String contaBancariaId,

        @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
    );

    @Operation(
        summary = "Consultar lançamentos por período",
        description = "Retorna os lançamentos financeiros de uma conta bancária dentro do período informado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lançamentos consultados com sucesso."),
        @ApiResponse(responseCode = "400", description = "Período informado inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Conta bancária não encontrada.")
    })
    ResponseEntity<List<LancamentoResDTO>> consultarLancamentosPorPeriodo(
        @Parameter(description = "Identificador da conta bancária.", required = true)
        @PathVariable String contaBancariaId,

        @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
    );
}
