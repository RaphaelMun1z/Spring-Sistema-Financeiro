package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
    List<Cartao> findByContaBancaria_Id(String idContaBancaria);
}
