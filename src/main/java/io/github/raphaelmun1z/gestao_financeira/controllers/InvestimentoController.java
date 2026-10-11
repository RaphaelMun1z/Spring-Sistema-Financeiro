package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.InvestimentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.InvestimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investimentos")
public class InvestimentoController {
    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InvestimentoResDTO> cadastrar(@RequestBody @Valid InvestimentoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<InvestimentoResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }
}
