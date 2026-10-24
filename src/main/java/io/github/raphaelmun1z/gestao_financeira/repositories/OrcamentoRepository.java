package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.YearMonth;

public interface OrcamentoRepository extends JpaRepository<Orcamento, String> {
    List<Orcamento> findByCategoriaId(String categoriaId);

    List<Orcamento> findByCategoria_AutorIdAndMesReferencia(String autorId, YearMonth mesReferencia);

    boolean existsByCategoriaId(String categoriaId);

    Optional<Orcamento> findByIdAndCategoria_AutorId(String id, String autorId);
}
