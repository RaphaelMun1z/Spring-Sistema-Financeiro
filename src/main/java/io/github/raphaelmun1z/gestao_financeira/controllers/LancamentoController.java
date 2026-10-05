package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {
    private final LancamentoService service;

    public LancamentoController(LancamentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<LancamentoResDTO> cadastrar(@RequestBody @Valid LancamentoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }
}
