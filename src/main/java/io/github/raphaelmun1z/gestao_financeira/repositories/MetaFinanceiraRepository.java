package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.meta.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, String> {
}
