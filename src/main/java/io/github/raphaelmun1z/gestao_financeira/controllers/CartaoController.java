package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.CartaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    private final CartaoService service;

    public CartaoController(CartaoService cartaoService) {
        this.service = cartaoService;
    }

    @PostMapping
    public ResponseEntity<CartaoResDTO> cadastrar(@RequestBody @Valid CartaoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<CartaoResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }
}
