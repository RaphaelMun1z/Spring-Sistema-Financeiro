package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.TelegramVinculoResDTO;
import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.TelegramControllerContract;
import io.github.raphaelmun1z.gestao_financeira.services.TelegramVinculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram")
public class TelegramController implements TelegramControllerContract {
    private final TelegramVinculoService service;

    public TelegramController(TelegramVinculoService service) {
        this.service = service;
    }

    @GetMapping("/vinculo")
    public ResponseEntity<TelegramVinculoResDTO> obterCodigo() {
        return ResponseEntity.ok(service.obterCodigo());
    }

    @PostMapping("/vinculo")
    public ResponseEntity<TelegramVinculoResDTO> gerarNovoCodigo() {
        return ResponseEntity.ok(service.alterarCodigo());
    }
}
