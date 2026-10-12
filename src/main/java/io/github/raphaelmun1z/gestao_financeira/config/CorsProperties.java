package io.github.raphaelmun1z.gestao_financeira.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(String allowedOrigin) {
}