package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginRequestDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LoginResponseDTO;
import io.github.raphaelmun1z.gestao_financeira.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/acessar")
    public ResponseEntity<LoginResponseDTO> acessar(@RequestBody @Valid LoginRequestDTO data) {
        return ResponseEntity.ok(service.login(data));
    }
}
