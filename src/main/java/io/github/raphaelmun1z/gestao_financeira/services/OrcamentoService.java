package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.OrcamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.Orcamento;
import io.github.raphaelmun1z.gestao_financeira.repositories.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;

import java.time.YearMonth;
import java.util.List;

@Service
public class OrcamentoService {
    private final OrcamentoRepository repository;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;

    public OrcamentoService(OrcamentoRepository repository, CategoriaMovimentacaoService categoriaMovimentacaoService) {
        this.repository = repository;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
    }

    @Transactional
    public OrcamentoResDTO cadastrar(String categoriaId, OrcamentoReqDTO data) {
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(categoriaId);

        Orcamento novoObj = new Orcamento(
            YearMonth.now(),
            data.valorLimite(),
            data.valorCorrente(),
            categoria
        );
        return new OrcamentoResDTO(repository.save(novoObj));
    }

    public List<OrcamentoResDTO> consultarPorCategoria(String categoriaId) {
        categoriaMovimentacaoService.buscarEntidadePorId(categoriaId);
        return repository.findByCategoriaId(categoriaId).stream().map(OrcamentoResDTO::new).toList();
    }

    public OrcamentoResDTO consultarPorId(String categoriaId, String orcamentoId) {
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(categoriaId);
        Orcamento orcamento = repository.findByIdAndCategoria_AutorId(orcamentoId, UsuarioService.obterUsuarioAutenticado().getId())
            .filter(item -> item.getCategoria().getId().equals(categoria.getId()))
            .orElseThrow(() -> new NotFoundException("Orçamento não encontrado nessa categoria!"));
        return new OrcamentoResDTO(orcamento);
    }

    @Transactional
    public OrcamentoResDTO atualizar(String categoriaId, String orcamentoId, OrcamentoReqDTO data) {
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(categoriaId);
        Orcamento orcamento = repository.findByIdAndCategoria_AutorId(orcamentoId, UsuarioService.obterUsuarioAutenticado().getId())
            .filter(item -> item.getCategoria().getId().equals(categoria.getId()))
            .orElseThrow(() -> new NotFoundException("Orçamento não encontrado!"));
        orcamento.setValorLimite(data.valorLimite());
        orcamento.setValorCorrente(data.valorCorrente());
        return new OrcamentoResDTO(orcamento);
    }

    @Transactional
    public void excluir(String categoriaId, String orcamentoId) {
        Orcamento orcamento = repository.findByIdAndCategoria_AutorId(orcamentoId, UsuarioService.obterUsuarioAutenticado().getId())
            .filter(item -> item.getCategoria().getId().equals(categoriaId))
            .orElseThrow(() -> new NotFoundException("Orçamento não encontrado!"));
        repository.delete(orcamento);
    }
}
