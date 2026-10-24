package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.InvestimentoControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.InvestimentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.InvestimentoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/investimentos")
public class InvestimentoController implements InvestimentoControllerContract {
    private final InvestimentoService service;

    public InvestimentoController(InvestimentoService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<InvestimentoResDTO> cadastrar(@RequestBody @Valid InvestimentoReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<InvestimentoResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @GetMapping("/{investimentoId}")
    public ResponseEntity<InvestimentoResDTO> consultarPorId(@PathVariable String investimentoId) {
        return ResponseEntity.ok(service.consultarPorId(investimentoId));
    }

    @PutMapping("/{investimentoId}")
    public ResponseEntity<InvestimentoResDTO> atualizar(@PathVariable String investimentoId, @RequestBody @Valid InvestimentoReqDTO data) {
        return ResponseEntity.ok(service.atualizar(investimentoId, data));
    }

    @DeleteMapping("/{investimentoId}")
    public ResponseEntity<Void> excluir(@PathVariable String investimentoId) {
        service.excluir(investimentoId);
        return ResponseEntity.noContent().build();
    }
}
