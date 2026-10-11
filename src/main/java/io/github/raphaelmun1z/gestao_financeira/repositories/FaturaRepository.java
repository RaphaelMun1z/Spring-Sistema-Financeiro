package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface FaturaRepository extends JpaRepository<Fatura, String> {
    List<Fatura> findByDataReferenciaBetween(YearMonth dataInicial, YearMonth dataFinal);
}
