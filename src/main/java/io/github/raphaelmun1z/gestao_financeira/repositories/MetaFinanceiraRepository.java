package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.MetaFinanceira;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MetaFinanceiraRepository extends JpaRepository<MetaFinanceira, String> {
    List<MetaFinanceira> findByCriadorId(String criadorId);

    Optional<MetaFinanceira> findByIdAndCriadorId(String id, String criadorId);
}
