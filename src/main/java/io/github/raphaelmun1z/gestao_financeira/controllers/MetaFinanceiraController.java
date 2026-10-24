package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.MetaFinanceiraControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.MetaFinanceiraReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.MetaFinanceiraService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metas-financeiras")
public class MetaFinanceiraController implements MetaFinanceiraControllerContract {
    private final MetaFinanceiraService service;

    public MetaFinanceiraController(MetaFinanceiraService service) {
        this.service = service;
    }

    @PostMapping
    @Override
    public ResponseEntity<MetaFinanceiraResDTO> cadastrar(@RequestBody @Valid MetaFinanceiraReqDTO data) {
        return ResponseEntity.ok(service.cadastrar(data));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<MetaFinanceiraResDTO>> consultarTodos() {
        return ResponseEntity.ok(service.consultarTodos());
    }

    @GetMapping("/{metaId}")
    public ResponseEntity<MetaFinanceiraResDTO> consultarPorId(@PathVariable String metaId) {
        return ResponseEntity.ok(service.consultarPorId(metaId));
    }

    @PutMapping("/{metaId}")
    public ResponseEntity<MetaFinanceiraResDTO> atualizar(@PathVariable String metaId, @RequestBody @Valid MetaFinanceiraReqDTO data) {
        return ResponseEntity.ok(service.atualizar(metaId, data));
    }

    @DeleteMapping("/{metaId}")
    public ResponseEntity<Void> excluir(@PathVariable String metaId) {
        service.excluir(metaId);
        return ResponseEntity.noContent().build();
    }
}
