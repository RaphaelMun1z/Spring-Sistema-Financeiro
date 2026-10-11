package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.RegistroDeMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroDeMovimentacaoRepository extends JpaRepository<RegistroDeMovimentacao, String> {
}
