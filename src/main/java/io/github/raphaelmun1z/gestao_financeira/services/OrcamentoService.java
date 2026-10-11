package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.OrcamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.OrcamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.Orcamento;
import io.github.raphaelmun1z.gestao_financeira.repositories.OrcamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repository.findByCategoriaId(categoriaId).stream().map(OrcamentoResDTO::new).toList();
    }
}
