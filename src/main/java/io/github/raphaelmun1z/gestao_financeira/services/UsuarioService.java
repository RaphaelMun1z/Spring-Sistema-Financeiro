package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.UserDetailsResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.repository = usuarioRepository;
    }

    public static Usuario obterUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder
            .getContext()
            .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado");
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
