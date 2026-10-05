package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaDeMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CategoriaMovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaMovimentacaoService {
    private final CategoriaMovimentacaoRepository repository;

    public CategoriaMovimentacaoService(CategoriaMovimentacaoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaDeMovimentacaoResDTO cadastrar(CategoriaDeMovimentacaoReqDTO data) {
        CategoriaDeMovimentacao novoObj = new CategoriaDeMovimentacao(
            data.nome(),
            data.tipo(),
            data.cor(),
            data.icone(),
            UsuarioService.obterUsuarioAutenticado()
        );
        return new CategoriaDeMovimentacaoResDTO(repository.save(novoObj));
    }

    public CategoriaDeMovimentacao buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));
    }

    public CategoriaDeMovimentacaoResDTO buscarPorId(String id) {
        return new CategoriaDeMovimentacaoResDTO(buscarEntidadePorId(id));
    }
}