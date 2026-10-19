package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroDeMovimentacaoRepository extends JpaRepository<RegistroDeMovimentacao, String> {
    List<RegistroDeMovimentacao> findByContaBancaria_IdAndDataLancamentoBetween(String contaBancariaId, LocalDate dataInicial, LocalDate dataFinal);
}
