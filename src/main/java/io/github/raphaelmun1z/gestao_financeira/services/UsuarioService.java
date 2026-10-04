package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.UserDetailsResponseDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.UsuarioRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.repository = usuarioRepository;
    }

    private Usuario findById(String id) {
        Optional<Usuario> obj = repository.findById(id);
        return obj.orElseThrow(() -> new NotFoundException(id));
    }

    private UserDetailsResponseDTO obterUsuarioAutenticado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new RuntimeException();
            }

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            if(userDetails == null) throw new RuntimeException();

            return new UserDetailsResponseDTO(userDetails);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    void excluirUsuarioAutenticado() {
        String usuarioAutenticadoId = null;
        try {
            usuarioAutenticadoId = this.obterUsuarioAutenticado().id();
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
