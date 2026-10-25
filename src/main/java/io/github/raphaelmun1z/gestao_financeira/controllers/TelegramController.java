package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.TelegramVinculoResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.TelegramVinculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telegram")
public class TelegramController {
    private final TelegramVinculoService service;

    public TelegramController(TelegramVinculoService service) {
        this.service = service;
    }

    @PostMapping("/vinculo")
    public ResponseEntity<TelegramVinculoResDTO> gerarCodigo() {
        return ResponseEntity.ok(service.gerarCodigo());
    }
}
