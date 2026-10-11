package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.CartaoService;
import io.github.raphaelmun1z.gestao_financeira.services.FaturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    private final CartaoService service;
    private final FaturaService faturaService;

    public CartaoController(CartaoService cartaoService, FaturaService faturaService) {
        this.service = cartaoService;
        this.faturaService = faturaService;
    }

    @PostMapping
    public ResponseEntity<CartaoResDTO> cadastrar(@RequestBody @Valid CartaoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<CartaoResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @GetMapping("/{cartaoId}/faturas")
    public ResponseEntity<List<FaturaResDTO>> consultarFaturasPorPeriodo(
        @PathVariable String cartaoId,
        @RequestBody @Valid ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(faturaService.consultarPorPeriodo(cartaoId, data));
    }
}
