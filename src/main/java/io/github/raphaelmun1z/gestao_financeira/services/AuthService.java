package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.TokenDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LoginRequestDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LoginResponseDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import io.github.raphaelmun1z.gestao_financeira.infra.security.TokenService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AuthService {
    @Lazy
    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            TokenDTO token = tokenService.generateToken((Usuario) Objects.requireNonNull(auth.getPrincipal()));
            return new LoginResponseDTO(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciais inválidas!");
        }
    }
}
