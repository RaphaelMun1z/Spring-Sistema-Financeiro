package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaMovimentacaoRepository extends JpaRepository<CategoriaDeMovimentacao, String> {
}
