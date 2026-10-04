package io.github.raphaelmun1z.gestao_financeira.dtos;

import java.time.Instant;

public class TokenDTO {
    private String username;
    private Instant created;
    private Instant expiration;
    private String accessToken;

    public TokenDTO() {
    }

    public TokenDTO(String username, Instant created, Instant expiration, String accessToken) {
        this.username = username;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}