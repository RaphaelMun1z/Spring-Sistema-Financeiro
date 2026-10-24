package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.ContaBancariaControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaAtualizacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.ContaBancariaService;
import io.github.raphaelmun1z.gestao_financeira.services.CartaoService;
import io.github.raphaelmun1z.gestao_financeira.services.FaturaService;
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
    private final CartaoService cartaoService;
    private final FaturaService faturaService;

    public ContaBancariaController(
        ContaBancariaService service,
        RegistroDeMovimentacaoService registroDeMovimentacaoService,
        LancamentoService lancamentoService,
        CartaoService cartaoService,
        FaturaService faturaService
    ) {
        this.service = service;
        this.registroDeMovimentacaoService = registroDeMovimentacaoService;
        this.lancamentoService = lancamentoService;
        this.cartaoService = cartaoService;
        this.faturaService = faturaService;
    }

    @PostMapping
    @Override
    public ResponseEntity<ContaBancariaResDTO> cadastrar(@RequestBody @Valid ContaBancariaReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<ContaBancariaResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @GetMapping("/{contaBancariaId}")
    public ResponseEntity<ContaBancariaResDTO> consultarPorId(@PathVariable String contaBancariaId) {
        return ResponseEntity.ok(service.consultarPorId(contaBancariaId));
    }

    @GetMapping("/{contaBancariaId}/cartoes/{cartaoId}")
    public ResponseEntity<CartaoResDTO> consultarCartaoPorId(@PathVariable String contaBancariaId, @PathVariable String cartaoId) {
        return ResponseEntity.ok(cartaoService.consultarPorContaEId(contaBancariaId, cartaoId));
    }

    @GetMapping("/{contaBancariaId}/cartoes/{cartaoId}/faturas/{faturaId}")
    public ResponseEntity<FaturaResDTO> consultarFaturaPorIdNoCaminho(@PathVariable String contaBancariaId, @PathVariable String cartaoId, @PathVariable String faturaId) {
        return ResponseEntity.ok(faturaService.consultarPorCaminho(contaBancariaId, cartaoId, faturaId));
    }

    @GetMapping("/{contaBancariaId}/cartoes/{cartaoId}/faturas")
    public ResponseEntity<List<FaturaResDTO>> consultarFaturasDoCartao(
        @PathVariable String contaBancariaId,
        @PathVariable String cartaoId,
        @Valid @ModelAttribute ConsultaPorPeriodoReqDTO data
    ) {
        cartaoService.consultarPorContaEId(contaBancariaId, cartaoId);
        return ResponseEntity.ok(faturaService.consultarPorCartaoEPeriodo(cartaoId, data));
    }

    @PutMapping("/{contaBancariaId}")
    public ResponseEntity<ContaBancariaResDTO> atualizar(@PathVariable String contaBancariaId, @RequestBody @Valid ContaBancariaAtualizacaoReqDTO data) {
        return ResponseEntity.ok(service.atualizar(contaBancariaId, data));
    }

    @DeleteMapping("/{contaBancariaId}")
    public ResponseEntity<Void> excluir(@PathVariable String contaBancariaId) {
        service.excluir(contaBancariaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{contaBancariaId}/registros-de-movimentacao")
    @Override
    public ResponseEntity<RegistroMovimentacaoResDTO> cadastrarMovimentacao(
        @PathVariable String contaBancariaId,
        @RequestBody @Valid RegistroMovimentacaoReqDTO data
    ) {
        return ResponseEntity.ok(registroDeMovimentacaoService.cadastrar(contaBancariaId, data));
    }

    @GetMapping("/{contaBancariaId}/registros-de-movimentacao")
    @Override
    public ResponseEntity<List<RegistroMovimentacaoResDTO>> consultarMovimentacoesPorContaBancariaEPeriodo(
        @PathVariable String contaBancariaId,
        @Valid @ModelAttribute ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(registroDeMovimentacaoService.consultarPorContaBancariaEPeriodo(contaBancariaId, data));
    }

    @GetMapping("/registros-de-movimentacao/{registroId}")
    @Override
    public ResponseEntity<RegistroMovimentacaoResDTO> consultarMovimentacaoPorId(@PathVariable String registroId) {
        return ResponseEntity.ok(registroDeMovimentacaoService.consultarPorId(registroId));
    }

    @PatchMapping("/registros-de-movimentacao/{registroId}")
    public ResponseEntity<RegistroMovimentacaoResDTO> atualizarMovimentacao(@PathVariable String registroId, @RequestBody @Valid io.github.raphaelmun1z.gestao_financeira.dtos.req.MovimentacaoAtualizacaoReqDTO data) {
        return ResponseEntity.ok(registroDeMovimentacaoService.atualizar(registroId, data));
    }

    @PatchMapping("/registros-de-movimentacao/{registroId}/recorrencia")
    public ResponseEntity<RegistroMovimentacaoResDTO> atualizarRecorrencia(@PathVariable String registroId, @RequestBody @Valid io.github.raphaelmun1z.gestao_financeira.dtos.req.RecorrenciaReqDTO data) {
        return ResponseEntity.ok(registroDeMovimentacaoService.atualizarRecorrencia(registroId, data));
    }

    @DeleteMapping("/registros-de-movimentacao/{registroId}")
    @Override
    public ResponseEntity<Void> excluirMovimentacao(@PathVariable String registroId) {
        registroDeMovimentacaoService.excluir(registroId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{contaBancariaId}/lancamentos")
    @Override
    public ResponseEntity<List<LancamentoResDTO>> consultarLancamentosPorPeriodo(
        @PathVariable String contaBancariaId,
        @ModelAttribute @Valid ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(lancamentoService.consultarPorPeriodo(contaBancariaId, data));
    }
}
