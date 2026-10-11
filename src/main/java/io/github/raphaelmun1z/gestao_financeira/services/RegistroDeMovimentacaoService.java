package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.entities.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistroDeMovimentacaoService {
    private final RegistroDeMovimentacaoRepository repository;

    public RegistroDeMovimentacaoService(RegistroDeMovimentacaoRepository repository) {
        this.repository = repository;
    }

    public RegistroDeMovimentacao cadastrar(RegistroDeMovimentacao data) {
        return repository.save(data);
    }

    public RegistroDeMovimentacao consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
    }
}
