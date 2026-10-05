package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LoginResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.UserDetailsResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.AuthService;
import io.github.raphaelmun1z.gestao_financeira.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    private final UsuarioService usuarioService;

    public AuthController(AuthService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/acessar")
    public ResponseEntity<LoginResDTO> acessar(@RequestBody @Valid LoginReqDTO data) {
        return ResponseEntity.ok(service.login(data));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailsResDTO> me() {
        return ResponseEntity.ok(UsuarioService.obterResumoUsuarioAutenticado());
    }

}
