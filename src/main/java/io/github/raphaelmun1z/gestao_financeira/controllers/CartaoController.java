package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.CartaoControllerContract;
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
public class CartaoController implements CartaoControllerContract {
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

    @GetMapping("/{contaBancariaId}")
    public ResponseEntity<List<CartaoResDTO>> consultarPorContaBancaria(@PathVariable String contaBancariaId) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(service.consultarPorContaBancaria(contaBancariaId));
    }

    @GetMapping("/{cartaoId}/faturas")
    public ResponseEntity<List<FaturaResDTO>> consultarFaturasPorPeriodo(
        @PathVariable String cartaoId,
        @Valid @ModelAttribute ConsultaPorPeriodoReqDTO data
    ) {
        return ResponseEntity.ok(faturaService.consultarPorPeriodo(cartaoId, data));
    }
}
