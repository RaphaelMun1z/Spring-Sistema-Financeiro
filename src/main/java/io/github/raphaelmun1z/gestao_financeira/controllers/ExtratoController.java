package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/extratos")
public class ExtratoController {
    private final LancamentoService service;

    public ExtratoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<LancamentoResDTO>> consultarPorPeriodo(@RequestBody @Valid ConsultaPorPeriodoReqDTO data) {
        return ResponseEntity.ok(service.consultarPorPeriodo(data));
    }
}
