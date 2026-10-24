package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
    @Query("select c from CartaoCredito c")
    List<CartaoCredito> findTodosCartoesDeCredito();

    @Query("select c from CartaoVoucher c")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<CartaoVoucher> findTodosCartoesVoucher();

    List<Cartao> findByContaBancaria_Id(String idContaBancaria);

    Optional<Cartao> findByIdAndContaBancaria_TitularId(String id, String titularId);

    Optional<Cartao> findByIdAndContaBancaria_Id(String id, String contaBancariaId);

    long countByContaBancaria_Id(String contaBancariaId);
}
