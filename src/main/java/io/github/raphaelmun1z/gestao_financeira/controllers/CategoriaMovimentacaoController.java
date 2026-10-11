package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.OrcamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.CategoriaMovimentacaoService;
import io.github.raphaelmun1z.gestao_financeira.services.OrcamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias-movimentacao")
public class CategoriaMovimentacaoController {
    private final CategoriaMovimentacaoService service;
    private final OrcamentoService orcamentoService;

    public CategoriaMovimentacaoController(
        CategoriaMovimentacaoService service,
        OrcamentoService orcamentoService
    ) {
        this.service = service;
        this.orcamentoService = orcamentoService;
    }

    @PostMapping
    public ResponseEntity<CategoriaMovimentacaoResDTO> cadastrar(@RequestBody @Valid CategoriaDeMovimentacaoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<CategoriaMovimentacaoResDTO> consultarPorId(@PathVariable String categoriaId) {
        return ResponseEntity.ok(service.consultarPorId(categoriaId));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaMovimentacaoResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @PostMapping("/{categoriaId}/orcamentos")
    public ResponseEntity<OrcamentoResDTO> cadastrarOrcamento(
        @PathVariable String categoriaId,
        @RequestBody @Valid OrcamentoReqDTO data
    ) {
        return ResponseEntity.ok(orcamentoService.cadastrar(categoriaId, data));
    }

    @GetMapping("/{categoriaId}/orcamentos")
    public ResponseEntity<List<OrcamentoResDTO>> consultarOrcamentos(@PathVariable String categoriaId) {
        return ResponseEntity.ok(orcamentoService.consultarPorCategoria(categoriaId));
    }
}