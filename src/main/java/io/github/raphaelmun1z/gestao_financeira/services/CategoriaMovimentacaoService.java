package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CategoriaDeMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CategoriaMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CategoriaMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.OrcamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaMovimentacaoService {
    private final CategoriaMovimentacaoRepository repository;
    private final OrcamentoRepository orcamentoRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;

    public CategoriaMovimentacaoService(CategoriaMovimentacaoRepository repository, OrcamentoRepository orcamentoRepository, RegistroDeMovimentacaoRepository registroRepository) {
        this.repository = repository;
        this.orcamentoRepository = orcamentoRepository;
        this.registroRepository = registroRepository;
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
        return repository.findByIdAndAutorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Categoria não encontrada!"));
    }

    public List<CategoriaMovimentacaoResDTO> consultarTodos() {
        return repository.findByAutorId(UsuarioService.obterUsuarioAutenticado().getId()).stream().map(CategoriaMovimentacaoResDTO::new).toList();
    }

    public CategoriaMovimentacaoResDTO consultarPorId(String categoriaId) {
        return new CategoriaMovimentacaoResDTO(buscarEntidadePorId(categoriaId));
    }

    @Transactional
    public CategoriaMovimentacaoResDTO atualizar(String id, CategoriaDeMovimentacaoReqDTO data) {
        CategoriaDeMovimentacao categoria = buscarEntidadePorId(id);
        if (categoria.getTipo() != data.tipo() && registroRepository.existsByCategoria_Id(id)) {
            throw new BusinessException("Não é possível alterar o tipo de uma categoria já utilizada.");
        }
        categoria.setNome(data.nome());
        categoria.setTipo(data.tipo());
        categoria.setCor(data.cor());
        categoria.setIcone(data.icone());
        return new CategoriaMovimentacaoResDTO(categoria);
    }

    @Transactional
    public void excluir(String id) {
        CategoriaDeMovimentacao categoria = buscarEntidadePorId(id);
        if (registroRepository.existsByCategoria_Id(id) || orcamentoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não é possível excluir uma categoria em uso.");
        }
        repository.delete(categoria);
    }
}
