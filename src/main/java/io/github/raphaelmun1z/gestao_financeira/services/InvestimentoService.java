package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.InvestimentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.investimento.Investimento;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import org.springframework.stereotype.Service;

@Service
public class InvestimentoService {
    private final InvestimentoRepository repository;

    public InvestimentoService(InvestimentoRepository repository) {
        this.repository = repository;
    }

    public InvestimentoResDTO cadastrar(InvestimentoReqDTO data){
        Investimento novoObj = new Investimento(
            data.categoria(),
            data.dataInicio(),
            data.valorInicial(),
            data.valorCorrente(),
            data.jurosAoMes()
        );
        return new InvestimentoResDTO(repository.save(novoObj));
    }
}
