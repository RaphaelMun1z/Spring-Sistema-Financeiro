package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.auth.UserDetailsResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.CadastroUsuarioReqDTO;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.DuplicatedResourceException;
import org.springframework.dao.DataIntegrityViolationException;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.repository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDetailsResDTO cadastrar(CadastroUsuarioReqDTO data) {
        String email = data.email().trim().toLowerCase(Locale.ROOT);
        String nome = data.nome().trim();
        if (data.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("A senha não pode exceder 72 bytes em UTF-8.");
        }
        if (repository.findByEmail(email).isPresent()) {
            throw new DuplicatedResourceException("Já existe um usuário cadastrado com este email.");
        }
        try {
            Usuario usuario = repository.saveAndFlush(new Usuario(
                nome, email, passwordEncoder.encode(data.senha()),
                io.github.raphaelmun1z.gestao_financeira.entities.enums.PapelDoUsuarioEnum.PADRAO
            ));
            return new UserDetailsResDTO(usuario);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicatedResourceException("Já existe um usuário cadastrado com este email.");
        }
    }

    public static Usuario obterUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Usuario)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Usuário não autenticado");
        }

        return (Usuario) auth.getPrincipal();
    }

    public static UserDetailsResDTO obterResumoUsuarioAutenticado() {
        return new UserDetailsResDTO(obterUsuarioAutenticado());
    }

    @Transactional
    public void excluirUsuarioAutenticado() {
        String usuarioAutenticadoId = null;
        try {
            usuarioAutenticadoId = obterResumoUsuarioAutenticado().id();
            if (repository.existsById(usuarioAutenticadoId)) {
                repository.deleteById(usuarioAutenticadoId);
            } else {
                throw new NotFoundException(usuarioAutenticadoId);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(usuarioAutenticadoId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(e.getMessage());
        }
    }
}
