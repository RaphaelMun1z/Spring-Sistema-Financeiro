
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
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "Consultar conta bancária por ID")
    ResponseEntity<ContaBancariaResDTO> consultarPorId(@PathVariable String contaBancariaId);

    @Operation(summary = "Consultar cartão por ID dentro da conta")
    ResponseEntity<io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoResDTO> consultarCartaoPorId(@PathVariable String contaBancariaId, @PathVariable String cartaoId);

    @Operation(summary = "Consultar fatura por ID dentro da conta e cartão")
    ResponseEntity<io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO> consultarFaturaPorIdNoCaminho(@PathVariable String contaBancariaId, @PathVariable String cartaoId, @PathVariable String faturaId);

    @Operation(summary = "Editar conta bancária", description = "Altera apenas o nome da conta; saldo e limite não são editados diretamente.")
    ResponseEntity<ContaBancariaResDTO> atualizar(@PathVariable String contaBancariaId, @RequestBody @Valid io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaAtualizacaoReqDTO data);

    @Operation(summary = "Excluir conta bancária", description = "Exclui a conta somente quando ela não possui cartões nem movimentações.")
    ResponseEntity<Void> excluir(@PathVariable String contaBancariaId);

    @Operation(summary = "Editar descrição da movimentação", description = "Altera apenas a descrição, preservando o histórico financeiro.")
    ResponseEntity<RegistroMovimentacaoResDTO> atualizarMovimentacao(@PathVariable String registroId, @RequestBody @Valid io.github.raphaelmun1z.gestao_financeira.dtos.req.MovimentacaoAtualizacaoReqDTO data);

    @Operation(summary = "Ativar ou cancelar recorrência", description = "Afeta somente futuras gerações; movimentações já geradas permanecem intactas.")
    ResponseEntity<RegistroMovimentacaoResDTO> atualizarRecorrencia(@PathVariable String registroId, @RequestBody @Valid io.github.raphaelmun1z.gestao_financeira.dtos.req.RecorrenciaReqDTO data);

    @Operation(summary = "Consultar movimentação", description = "Retorna uma movimentação pertencente ao usuário autenticado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimentação consultada com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Movimentação não encontrada.")
    })
    ResponseEntity<RegistroMovimentacaoResDTO> consultarMovimentacaoPorId(
        @Parameter(description = "Identificador da movimentação.", required = true)
        @PathVariable String registroId
    );

    @Operation(summary = "Cancelar movimentação",
        description = "Exclui a movimentação e suas parcelas, recalcula faturas e estorna saldo ou limite. Movimentações com fatura paga não podem ser excluídas.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Movimentação cancelada."),
        @ApiResponse(responseCode = "400", description = "Fatura paga ou saldo insuficiente para estornar uma entrada."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(responseCode = "404", description = "Movimentação não encontrada.")
    })
    ResponseEntity<Void> excluirMovimentacao(
        @Parameter(description = "Identificador da movimentação.", required = true)
        @PathVariable String registroId
    );

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
        description = "Atualiza saldo ou limite e gera lancamentos atomicamente. Credito exige 1 a 120 parcelas; compra no dia do fechamento entra no proximo ciclo. Voucher utiliza saldo proprio. dataLancamento deve ser hoje ou uma data passada. Quando ehRecorrente=true, a primeira repeticao e prevista para um mes apos dataLancamento e as seguintes sao geradas mensalmente sem data final."
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

        @ParameterObject @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
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

        @ParameterObject @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
    );
}
