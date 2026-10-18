package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.ContaBancariaControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.ContaBancariaService;
import io.github.raphaelmun1z.gestao_financeira.services.LancamentoService;
import io.github.raphaelmun1z.gestao_financeira.services.RegistroDeMovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas-bancarias")
public class ContaBancariaController implements ContaBancariaControllerContract {
    private final ContaBancariaService service;
    private final RegistroDeMovimentacaoService registroDeMovimentacaoService;
    private final LancamentoService lancamentoService;

    public ContaBancariaController(
        ContaBancariaService service,
        RegistroDeMovimentacaoService registroDeMovimentacaoService,
        LancamentoService lancamentoService
    ) {
        this.service = service;
        this.registroDeMovimentacaoService = registroDeMovimentacaoService;
        this.lancamentoService = lancamentoService;
    }

    @PostMapping
    public ResponseEntity<ContaBancariaResDTO> cadastrar(@RequestBody @Valid ContaBancariaReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<ContaBancariaResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @PostMapping("/{contaBancariaId}/registros-de-movimentacao")
    public ResponseEntity<RegistroMovimentacaoResDTO> cadastrarMovimentacao(
        @PathVariable String contaBancariaId,
        @RequestBody @Valid RegistroMovimentacaoReqDTO data
    ) {
        return ResponseEntity.ok(registroDeMovimentacaoService.cadastrar(contaBancariaId, data));
    }

    @GetMapping("/{contaBancariaId}/registros-de-movimentacao")
    public ResponseEntity<List<RegistroMovimentacaoResDTO>> consultarMovimentacoesPorPeriodo(
        @PathVariable String contaBancariaId,
        @Valid @ModelAttribute ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(registroDeMovimentacaoService.consultarPorPeriodo(contaBancariaId, data));
    }

    @GetMapping("/{contaBancariaId}/lancamentos")
    public ResponseEntity<List<LancamentoResDTO>> consultarLancamentosPorPeriodo(
        @PathVariable String contaBancariaId,
        @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(lancamentoService.consultarPorPeriodo(contaBancariaId, data));
    }
}
