package io.github.raphaelmun1z.gestao_financeira.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Finanças API", version = "v1", description = "Documentação da API RESTful para o sistema Finanças.", termsOfService = "https://github.com/RaphaelMun1z", license = @License(name = "Apache 2.0", url = "https://github.com/RaphaelMun1z")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class OpenApiConfig {

}