package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroDeMovimentacaoService {
    private final RegistroDeMovimentacaoRepository repository;

    public RegistroDeMovimentacaoService(RegistroDeMovimentacaoRepository repository) {
        this.repository = repository;
    }

    public RegistroMovimentacaoResDTO cadastrar(String contaBancariaId, RegistroMovimentacaoReqDTO data) {
        return null;
    }

    public RegistroDeMovimentacao consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
    }

    public List<RegistroMovimentacaoResDTO> consultarPorPeriodo(String contaBancariaId, ConsultaPorPeriodoReqDTO data) {
        List<RegistroDeMovimentacao> registros = repository.findByDataLancamentoBetween(data.dataInicial(), data.dataFinal());
        return registros.stream().map(RegistroMovimentacaoResDTO::new).toList();
    }
}
