package io.github.raphaelmun1z.gestao_financeira.entities.usuario;

import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.meta.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.enums.PapelDoUsuarioEnum;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "tb_usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String email;

    private String senha;
    private String nomeCompleto;
    private Set<CategoriaDeMovimentacao> categoriasDeMovimentacao = new HashSet<>();
    private Set<ContaBancaria> contasBancarias = new HashSet<>();
    private List<MetaFinanceira> metasFinanceiras = new ArrayList<>();
    private PapelDoUsuarioEnum papel;

    public Usuario() {
    }

    public Usuario(String nomeCompleto, String email, String senha, PapelDoUsuarioEnum papel) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        setPapel(papel);
    }

    private void setPapel(PapelDoUsuarioEnum papel) {
        if (papel == null) {
            throw new IllegalStateException("Papel não pode ser nulo.");
        }

        this.papel = papel;
    }

    public String getPapel() {
        return papel.getPapel();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (papel == PapelDoUsuarioEnum.PAPEL_PADRAO) {
            return List.of(new SimpleGrantedAuthority("ROLE_PADRAO"));
        } else if (papel == PapelDoUsuarioEnum.PAPEL_ADMINISTRADOR) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"),
                new SimpleGrantedAuthority("ROLE_PADRAO")
            );
        } else {
            throw new IllegalArgumentException("Valor inesperado: " + this.papel);
        }
    }

    public String getId() {
        return id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<CategoriaDeMovimentacao> getCategoriasDeMovimentacao() {
        return categoriasDeMovimentacao;
    }

    public Set<ContaBancaria> getContasBancarias() {
        return contasBancarias;
    }

    public List<MetaFinanceira> getMetasFinanceiras() {
        return metasFinanceiras;
    }

    @Override
    public @Nullable String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
