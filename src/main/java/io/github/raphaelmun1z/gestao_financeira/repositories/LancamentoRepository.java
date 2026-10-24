package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LancamentoRepository extends JpaRepository<Lancamento, String> {
    List<Lancamento> findTop10ByRegistroDeMovimentacao_ContaBancaria_TitularIdOrderByDataLancamentoDesc(String titularId);

    List<Lancamento> findByRegistroDeMovimentacao_ContaBancaria_TitularIdAndDataLancamentoBetweenOrderByDataLancamentoDesc(
        String titularId, LocalDate dataInicial, LocalDate dataFinal);

    List<Lancamento> findByRegistroDeMovimentacao_ContaBancaria_IdAndDataLancamentoBetween(
        String contaBancariaId, LocalDate dataInicial, LocalDate dataFinal);
}
