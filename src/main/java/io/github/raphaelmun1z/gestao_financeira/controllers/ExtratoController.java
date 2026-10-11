package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaExtratoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.LancamentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extratos")
public class ExtratoController {
    private final LancamentoService service;

    public ExtratoController(LancamentoService service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<List<LancamentoResDTO>> consultarExtratoPorPeriodo(@RequestBody @Valid ConsultaExtratoReqDTO data) {
        return ResponseEntity.ok(service.consultarExtratoPorPeriodo(data));
    }
}
