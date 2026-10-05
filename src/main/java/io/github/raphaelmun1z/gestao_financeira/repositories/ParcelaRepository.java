package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelaRepository extends JpaRepository<Parcela, String> {
}
