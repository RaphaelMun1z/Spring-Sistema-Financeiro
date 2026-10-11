package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

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

    public List<FaturaResDTO> consultarPorPeriodo(String cartaoId, ConsultaPorPeriodoReqDTO data) {
        List<Fatura> faturas = repository.findByDataReferenciaBetween(YearMonth.from(data.dataInicial()), YearMonth.from(data.dataFinal()));
        return faturas.stream().map(FaturaResDTO::new).toList();
    }
}
