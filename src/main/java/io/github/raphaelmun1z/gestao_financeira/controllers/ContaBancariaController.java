package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.ContaBancariaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas-bancarias")
public class ContaBancariaController {
    private final ContaBancariaService contaBancariaService;

    public ContaBancariaController(ContaBancariaService contaBancariaService) {
        this.contaBancariaService = contaBancariaService;
    }

    @PostMapping
    public ResponseEntity<ContaBancariaResDTO> cadastrar(@RequestBody @Valid ContaBancariaReqDTO data) {
        return ResponseEntity.ok(contaBancariaService.cadastrar(data));
    }

    @GetMapping
    public ResponseEntity<List<ContaBancariaResDTO>> consultarTodos() {
        return ResponseEntity.ok(contaBancariaService.consultarTodos());
    }
}
