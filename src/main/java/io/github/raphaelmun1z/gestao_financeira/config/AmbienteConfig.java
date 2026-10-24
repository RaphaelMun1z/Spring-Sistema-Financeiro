package io.github.raphaelmun1z.gestao_financeira.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

@Configuration
public class AmbienteConfig {
    public AmbienteConfig(Environment environment) {
        if (environment.acceptsProfiles(Profiles.of("prod")) && environment.acceptsProfiles(Profiles.of("dev"))) {
            throw new IllegalStateException("Os perfis dev e prod não podem ser ativados simultaneamente.");
        }
        if (!environment.acceptsProfiles(Profiles.of("dev", "test"))) {
            for (String property : new String[] {
                "spring.datasource.url", "spring.datasource.username", "spring.datasource.password", "api.security.token.secret"
            }) {
                String value = environment.getRequiredProperty(property);
                if (value.isBlank() || value.contains("${")) {
                    throw new IllegalStateException("Configuração obrigatória ausente: " + property);
                }
            }
        }
    }
}
