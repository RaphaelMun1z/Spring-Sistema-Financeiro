package io.github.raphaelmun1z.gestao_financeira.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.github.raphaelmun1z.gestao_financeira.dtos.TokenDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class TokenService {
    @Value("${api.security.token.secret:secret}")
    private String secret;

    private final int HOURS_TOKEN_EXPIRE = 12;

    Algorithm algorithm = null;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public TokenDTO generateToken(Usuario user) {
        try {
            String username = user.getUsername();
            Instant createdAt = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).toInstant();
            Instant expiration = genExpirationDate();
            String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            String token = JWT.create()
                .withClaim("role", user.getPapel())
                .withIssuedAt(createdAt)
                .withExpiresAt(expiration)
                .withIssuer(issuerUrl)
                .withSubject(user.getId())
                .sign(algorithm).strip();
            return new TokenDTO(username, createdAt, expiration, token);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro durante geração do token.", exception);
        }
    }

    public String validateToken(String token) {
        try {
            String issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

            return JWT.require(algorithm)
                .withIssuer(issuerUrl)
                .build()
                .verify(token)
                .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant genExpirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Sao_Paulo")).plusHours(HOURS_TOKEN_EXPIRE).toInstant();
    }
}