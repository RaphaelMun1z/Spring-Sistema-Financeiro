package io.github.raphaelmun1z.gestao_financeira.config;

import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
//@Profile("dev")
public class DataMock implements CommandLineRunner {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DataMock(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String nome = "Administrador";
        String email = "admin@gmail.com";
        String senha = "admin123";
        String senhaHash = passwordEncoder.encode(senha);
        PapelDoUsuarioEnum papel = PapelDoUsuarioEnum.PADRAO;

        if (repository.findByEmail(email).isEmpty()) {
            Usuario novoUsuario = new Usuario(nome, email, senhaHash, papel);
            repository.save(novoUsuario);
        }
    }

}