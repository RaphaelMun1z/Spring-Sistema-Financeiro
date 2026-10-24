package io.github.raphaelmun1z.gestao_financeira.controllers;

import io.github.raphaelmun1z.gestao_financeira.controllers.contracts.AuthControllerContract;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.CadastroUsuarioReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.LoginResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.UserDetailsResDTO;
import io.github.raphaelmun1z.gestao_financeira.services.AuthService;
import io.github.raphaelmun1z.gestao_financeira.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/cadastrar")
    @Override
    public ResponseEntity<UserDetailsResDTO> cadastrar(@RequestBody @Valid CadastroUsuarioReqDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(data));
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

    @DeleteMapping("/me")
    public ResponseEntity<Void> excluirMinhaConta() {
        usuarioService.excluirUsuarioAutenticado();
        return ResponseEntity.noContent().build();
    }

}
