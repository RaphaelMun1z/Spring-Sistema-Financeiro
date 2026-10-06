package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.RegistroDeDespesas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroDeDespesasRepository extends JpaRepository<RegistroDeDespesas, String> {
}
