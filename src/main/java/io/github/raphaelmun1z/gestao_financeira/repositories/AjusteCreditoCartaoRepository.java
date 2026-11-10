package io.github.raphaelmun1z.gestao_financeira.repositories;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.AjusteCreditoCartao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AjusteCreditoCartaoRepository extends JpaRepository<AjusteCreditoCartao, String> {
    Optional<AjusteCreditoCartao> findFirstByCartaoCredito_IdOrderByRegistradoEmDescIdDesc(String cartaoCreditoId);
}
