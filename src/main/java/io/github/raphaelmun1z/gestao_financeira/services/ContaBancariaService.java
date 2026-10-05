package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaBancariaService {
    private final ContaBancariaRepository repository;

    public ContaBancariaService(ContaBancariaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContaBancariaResDTO cadastrar(ContaBancariaReqDTO data){
        ContaBancaria novoObj = new ContaBancaria(
            data.nomeDoBanco(),
            data.saldoCorrente(),
            data.creditoTotal(),
            data.creditoRestante()
        );
        return new ContaBancariaResDTO(repository.save(novoObj));
    }
}
