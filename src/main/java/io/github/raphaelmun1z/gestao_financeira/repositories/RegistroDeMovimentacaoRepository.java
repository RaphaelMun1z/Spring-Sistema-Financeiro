package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroDeMovimentacaoRepository extends JpaRepository<RegistroDeMovimentacao, String> {
    @Query("select r.contaBancaria.id from RegistroDeMovimentacao r where r.id = :id and r.contaBancaria.titular.id = :titularId")
    Optional<String> buscarContaBancariaId(String id, String titularId);

    Optional<RegistroDeMovimentacao> findByIdAndContaBancaria_TitularId(String id, String titularId);
    List<RegistroDeMovimentacao> findByContaBancaria_IdAndDataLancamentoBetween(String contaBancariaId, LocalDate dataInicial, LocalDate dataFinal);

    List<RegistroDeMovimentacao> findByContaBancaria_TitularIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(
        String titularId, LocalDate dataInicial, LocalDate dataFinal);

    List<RegistroDeMovimentacao> findTop10ByContaBancaria_TitularIdOrderByDataLancamentoDesc(String titularId);

    boolean existsByContaBancaria_Id(String contaBancariaId);

    boolean existsByCartao_Id(String cartaoId);

    boolean existsByCategoria_Id(String categoriaId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RegistroDeMovimentacao r where r.ehRecorrente = true and r.proximaRecorrencia <= :data")
    List<RegistroDeMovimentacao> buscarRecorrenciasPendentes(@Param("data") LocalDate data);
}
