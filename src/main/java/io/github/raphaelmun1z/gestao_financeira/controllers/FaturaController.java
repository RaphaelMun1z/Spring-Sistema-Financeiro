package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.services.FaturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faturas")
public class FaturaController {
    private final FaturaService service;

    public FaturaController(FaturaService service) {
        this.service = service;
    }

}
