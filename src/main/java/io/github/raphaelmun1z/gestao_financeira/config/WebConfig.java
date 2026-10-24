package io.github.raphaelmun1z.gestao_financeira.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class WebConfig implements WebMvcConfigurer {
    private final CorsProperties properties;

    public WebConfig(CorsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (properties.allowedOrigin() == null || properties.allowedOrigin().isBlank()) {
            return;
        }
        registry.addMapping("/**")
            .allowedOrigins(java.util.Arrays.stream(properties.allowedOrigin().split(","))
                .map(String::trim).filter(origin -> !origin.isEmpty()).toArray(String[]::new))
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type", "Range")
            .exposedHeaders("Content-Type", "Content-Length", "Content-Range", "Accept-Ranges", "Content-Disposition", "ETag", "Last-Modified")
            .allowCredentials(true);
    }
}
