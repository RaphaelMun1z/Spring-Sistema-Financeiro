package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.Investimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface InvestimentoRepository extends JpaRepository<Investimento, String> {
    List<Investimento> findByInvestidorId(String investidorId);

    Optional<Investimento> findByIdAndInvestidorId(String id, String investidorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Investimento i")
    List<Investimento> findTodosParaAgendamento();
}
