package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.AuthControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.LoginResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.UserDetailsResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.AuthService;
import io.github.raphaelmun1z.gestao_financeira.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerContract {
    private final AuthService service;
    private final UsuarioService usuarioService;

    public AuthController(AuthService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/acessar")
    @Override
    public ResponseEntity<LoginResDTO> acessar(@RequestBody @Valid LoginReqDTO data) {
        return ResponseEntity.ok(service.login(data));
    }

    @GetMapping("/me")
    @Override
    public ResponseEntity<UserDetailsResDTO> me() {
        return ResponseEntity.ok(UsuarioService.obterResumoUsuarioAutenticado());
    }

}
