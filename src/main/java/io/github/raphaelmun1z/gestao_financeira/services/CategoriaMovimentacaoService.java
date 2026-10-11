package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CategoriaMovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaMovimentacaoService {
    private final CategoriaMovimentacaoRepository repository;

    public CategoriaMovimentacaoService(CategoriaMovimentacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaMovimentacaoResDTO cadastrar(CategoriaDeMovimentacaoReqDTO data) {
        CategoriaDeMovimentacao novoObj = new CategoriaDeMovimentacao(
            data.nome(),
            data.tipo(),
            data.cor(),
            data.icone(),
            UsuarioService.obterUsuarioAutenticado()
        );
        return new CategoriaMovimentacaoResDTO(repository.save(novoObj));
    }

    public CategoriaDeMovimentacao buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));
    }

    public List<CategoriaMovimentacaoResDTO> consultarTodos() {
        return repository.findAll().stream().map(CategoriaMovimentacaoResDTO::new).toList();
    }

    public CategoriaMovimentacaoResDTO consultarPorId(String categoriaId) {
        return new CategoriaMovimentacaoResDTO(buscarEntidadePorId(categoriaId));
    }
}