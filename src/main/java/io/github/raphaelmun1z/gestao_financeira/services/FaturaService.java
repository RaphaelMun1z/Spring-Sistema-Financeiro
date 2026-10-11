package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.entities.Fatura;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import org.springframework.stereotype.Service;

@Service
public class FaturaService {
    private final FaturaRepository repository;

    public FaturaService(FaturaRepository repository) {
        this.repository = repository;
    }

    public Fatura cadastrar(Fatura data) {
        return repository.save(data);
    }

    public Fatura consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada!"));
    }
}
