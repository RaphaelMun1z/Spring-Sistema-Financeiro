package io.github.raphaelmun1z.gestao_financeira.controllers.contracts;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.DashboardResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.dashboard.PeriodosHistoricoResDTO;
import io.github.raphaelmun1z.gestao_financeira.exceptions.ExceptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.YearMonth;

@Tag(name = "Dashboard", description = "Visão consolidada da situação financeira do usuário.")
@SecurityRequirement(name = "bearerAuth")
public interface DashboardControllerContract {
    @Operation(
        summary = "Consultar dashboard financeiro",
        description = "Retorna saldos, crédito, faturas, contas, movimentações, lançamentos, investimentos, metas e orçamentos do usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard consultado com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado."),
        @ApiResponse(
            responseCode = "422",
            description = "Não há saldo histórico disponível para o período informado.",
            content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
        )
    })
    ResponseEntity<DashboardResDTO> consultar(
        @Parameter(description = "Mês de referência no formato yyyy-MM. Padrão: mês atual.", example = "2026-10")
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth mesReferencia
    );

    @Operation(
        summary = "Consultar períodos com histórico",
        description = "Retorna os meses que possuem snapshots de todas as contas do usuário autenticado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Períodos históricos consultados com sucesso."),
        @ApiResponse(responseCode = "401", description = "Usuário não autenticado.")
    })
    ResponseEntity<PeriodosHistoricoResDTO> consultarPeriodosComHistorico();
}
