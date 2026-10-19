package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface FaturaRepository extends JpaRepository<Fatura, String> {
    List<Fatura> findByCartaoDeCredito_IdAndDataReferenciaBetween(String cartaoID, YearMonth dataInicial, YearMonth dataFinal);

    Optional<Fatura> findByCartaoDeCredito_IdAndDataReferencia(
        String cartaoId,
        YearMonth dataReferencia
    );
}
