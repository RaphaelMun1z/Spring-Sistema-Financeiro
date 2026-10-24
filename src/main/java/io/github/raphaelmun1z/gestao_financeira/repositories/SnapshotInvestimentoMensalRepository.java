package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.SnapshotInvestimentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;

public interface SnapshotInvestimentoMensalRepository extends JpaRepository<SnapshotInvestimentoMensal, String> {
    List<SnapshotInvestimentoMensal> findByInvestimento_InvestidorIdAndMesReferencia(String investidorId, YearMonth mesReferencia);
}
