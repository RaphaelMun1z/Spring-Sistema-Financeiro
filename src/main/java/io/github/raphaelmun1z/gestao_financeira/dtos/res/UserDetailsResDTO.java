package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserDetailsResDTO(
    String id,
    String nomeCompleto,
    String email,
    String papel,
    List<String> authorities
) {
    public UserDetailsResDTO(Usuario user) {
        this(
            user.getId(),
            user.getNomeCompleto(),
            user.getEmail(),
            user.getPapel(),
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );
    }
}
