package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.SnapshotFinanceiroMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface SnapshotFinanceiroMensalRepository extends JpaRepository<SnapshotFinanceiroMensal, String> {
    Optional<SnapshotFinanceiroMensal> findByContaBancaria_IdAndMesReferencia(String contaBancariaId, YearMonth mesReferencia);
    List<SnapshotFinanceiroMensal> findByContaBancaria_TitularId(String titularId);
    List<SnapshotFinanceiroMensal> findByContaBancaria_TitularIdAndMesReferencia(String titularId, YearMonth mesReferencia);
    boolean existsByContaBancaria_IdAndMesReferencia(String contaBancariaId, YearMonth mesReferencia);
}
