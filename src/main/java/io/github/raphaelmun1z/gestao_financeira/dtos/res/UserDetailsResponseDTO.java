package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public record UserDetailsResponseDTO(
    String id,
    String nomeCompleto,
    String email,
    String papel,
    List<String> authorities
) {
    public UserDetailsResponseDTO(UserDetails user) {
        this(
            ((Usuario) user).getId(),
            ((Usuario) user).getNomeCompleto(),
            ((Usuario) user).getEmail(),
            ((Usuario) user).getPapel(),
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );
    }
}
