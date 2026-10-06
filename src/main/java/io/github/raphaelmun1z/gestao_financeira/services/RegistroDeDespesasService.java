package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.RegistroDeDespesas;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeDespesasRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistroDeDespesasService {
    private final RegistroDeDespesasRepository repository;

    public RegistroDeDespesasService(RegistroDeDespesasRepository repository) {
        this.repository = repository;
    }

    public RegistroDeDespesas buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de despesas não encontrado!"));
    }
}
