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
    @Override
    public ResponseEntity<CartaoResDTO> cadastrar(@RequestBody @Valid CartaoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping("/{contaBancariaId}")
    @Override
    public ResponseEntity<List<CartaoResDTO>> consultarPorContaBancaria(@PathVariable String contaBancariaId) {
        return ResponseEntity.ok(service.consultarPorContaBancaria(contaBancariaId));
    }

    @PutMapping("/{cartaoId}")
    public ResponseEntity<CartaoResDTO> atualizar(@PathVariable String cartaoId, @RequestBody @Valid CartaoReqDTO data) {
        return ResponseEntity.ok(service.atualizar(cartaoId, data));
    }

    @DeleteMapping("/{cartaoId}")
    public ResponseEntity<Void> excluir(@PathVariable String cartaoId) {
        service.excluir(cartaoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/faturas/{faturaId}/pagamento")
    @Override
    public ResponseEntity<FaturaResDTO> pagarFatura(@PathVariable String faturaId) {
        return ResponseEntity.ok(faturaService.pagar(faturaId));
    }
}
