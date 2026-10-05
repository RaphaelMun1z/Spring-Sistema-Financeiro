package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaDeMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.CategoriaMovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categorias-movimentacao")
public class CategoriaMovimentacaoController {
    private final CategoriaMovimentacaoService service;

    public CategoriaMovimentacaoController(CategoriaMovimentacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CategoriaDeMovimentacaoResDTO> cadastrar(@RequestBody @Valid CategoriaDeMovimentacaoReqDTO data){
        return ResponseEntity.ok(service.cadastrar(data));
    }
}