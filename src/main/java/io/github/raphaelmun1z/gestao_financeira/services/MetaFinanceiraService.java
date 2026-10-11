package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.MetaFinanceiraReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.repositories.MetaFinanceiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetaFinanceiraService {
    private final MetaFinanceiraRepository repository;

    public MetaFinanceiraService(MetaFinanceiraRepository repository) {
        this.repository = repository;
    }

    public MetaFinanceiraResDTO cadastrar(MetaFinanceiraReqDTO data){
        MetaFinanceira novoObj = new MetaFinanceira(
            data.objetivo(),
            data.valorAlvo(),
            data.valorAcumulado(),
            data.dataConclusaoPrevista()
        );
        return new MetaFinanceiraResDTO(repository.save(novoObj));
    }

    public List<MetaFinanceiraResDTO> consultarTodos() {
        return repository.findAll().stream().map(MetaFinanceiraResDTO::new).toList();
    }
}
