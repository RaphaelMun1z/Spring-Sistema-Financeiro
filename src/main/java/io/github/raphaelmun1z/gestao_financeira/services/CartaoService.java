package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import org.springframework.stereotype.Service;

@Service
public class CartaoService {
    private final CartaoRepository repository;

    public CartaoService(CartaoRepository cartaoRepository) {
        this.repository = cartaoRepository;
    }

    public Cartao buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado!"));
    }

//    public CartaoResDTO buscarPorId(String id) {
//        return new CartaoResDTO(buscarEntidadePorId(id));
//    }
}
