package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaMovimentacaoRepository extends JpaRepository<CategoriaDeMovimentacao, String> {
    @EntityGraph(attributePaths = "orcamentos")
    List<CategoriaDeMovimentacao> findAll();

    @EntityGraph(attributePaths = "orcamentos")
    Optional<CategoriaDeMovimentacao> findById(String id);
}
