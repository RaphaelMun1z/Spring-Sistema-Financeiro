package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, String> {
    List<ContaBancaria> findByTitularId(String idTitular);
}
