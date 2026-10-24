package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.PagamentoFatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PagamentoFaturaRepository extends JpaRepository<PagamentoFatura, String> {
    List<PagamentoFatura> findByContaBancaria_IdAndDataPagamentoAfter(String contaBancariaId, LocalDate data);
}
