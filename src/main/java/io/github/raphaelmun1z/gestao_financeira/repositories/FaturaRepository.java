package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import java.math.BigDecimal;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface FaturaRepository extends JpaRepository<Fatura, String> {
    @Query("select f from Fatura f where f.cartaoDeCredito.contaBancaria.titular.id = :titularId")
    List<Fatura> findByTitularId(@Param("titularId") String titularId);

    @Query("select f.cartaoDeCredito.contaBancaria.id from Fatura f where f.id = :id and f.cartaoDeCredito.contaBancaria.titular.id = :titularId")
    Optional<String> buscarContaBancariaId(String id, String titularId);

    Optional<Fatura> findByIdAndCartaoDeCredito_ContaBancaria_TitularId(String id, String titularId);

    Optional<Fatura> findByIdAndCartaoDeCredito_IdAndCartaoDeCredito_ContaBancaria_IdAndCartaoDeCredito_ContaBancaria_TitularId(
        String id, String cartaoId, String contaBancariaId, String titularId);

    @Query("select coalesce(sum(f.valorTotal), 0) from Fatura f where f.cartaoDeCredito.id = :cartaoId and f.status <> :status")
    BigDecimal somarTotalPorCartaoEStatusDiferente(String cartaoId, StatusFaturaEnum status);
    List<Fatura> findByCartaoDeCredito_IdAndDataReferenciaBetween(String cartaoID, YearMonth dataInicial, YearMonth dataFinal);

    Optional<Fatura> findByCartaoDeCredito_IdAndDataReferencia(
        String cartaoId,
        YearMonth dataReferencia
    );

    List<Fatura> findByStatusNot(StatusFaturaEnum status);

    boolean existsByCartaoDeCredito_Id(String cartaoId);

    List<Fatura> findByCartaoDeCredito_Id(String cartaoId);

    @Modifying
    @Query("update Fatura f set f.status = :status where f.id = :id and f.status <> :paga")
    int atualizarStatusSeNaoPaga(
        @Param("id") String id,
        @Param("status") StatusFaturaEnum status,
        @Param("paga") StatusFaturaEnum paga
    );
}
