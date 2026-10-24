package io.github.raphaelmun1z.gestao_financeira;

import io.github.raphaelmun1z.gestao_financeira.config.AmbienteConfig;
import io.github.raphaelmun1z.gestao_financeira.infra.security.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfiguracaoTests {
    @Test
    void recusarProducaoSemCredenciaisEDesenvolvimentoSimultaneo() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("prod");
        assertThatThrownBy(() -> new AmbienteConfig(environment)).isInstanceOf(IllegalStateException.class);
        environment.setActiveProfiles("dev", "prod");
        assertThatThrownBy(() -> new AmbienteConfig(environment)).hasMessageContaining("simultaneamente");
    }

    @Test
    void recusarSegredoJwtVazioOuCurto() {
        for (String secret : new String[] {"", "chave-jwt", "secret"}) {
            TokenService service = new TokenService();
            ReflectionTestUtils.setField(service, "secret", secret);
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(service, "init"))
                .hasMessageContaining("32 bytes");
        }
    }
}
