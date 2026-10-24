package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, String> {
    List<ContaBancaria> findByTitularId(String idTitular);

    Optional<ContaBancaria> findByIdAndTitularId(String id, String titularId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancaria c where c.id = :id and c.titular.id = :titularId")
    Optional<ContaBancaria> buscarPorIdParaAtualizar(String id, String titularId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from ContaBancaria c where c.id = :id")
    Optional<ContaBancaria> buscarPorIdParaAgendamento(@Param("id") String id);
}
