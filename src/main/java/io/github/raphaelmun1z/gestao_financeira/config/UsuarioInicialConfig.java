package io.github.raphaelmun1z.gestao_financeira.config;

import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ConditionalOnProperty(prefix = "app.usuario-inicial", name = "enabled", havingValue = "true")
public class UsuarioInicialConfig implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String senha;
    private final String nome;

    public UsuarioInicialConfig(
        UsuarioRepository repository,
        PasswordEncoder passwordEncoder,
        @Value("${app.usuario-inicial.email}") String email,
        @Value("${app.usuario-inicial.senha}") String senha,
        @Value("${app.usuario-inicial.nome}") String nome
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    @Override
    public void run(String... args) {
        if (email.isBlank() || !email.contains("@") || nome.isBlank() || senha.isBlank()
            || senha.length() < 12 || senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Informe nome, email e senha de 12 caracteres a 72 bytes para o usuário inicial.");
        }
        if (repository.findByEmail(email).isEmpty()) {
            repository.save(new Usuario(nome, email, passwordEncoder.encode(senha), PapelDoUsuarioEnum.ADMINISTRADOR));
        }
    }
}
