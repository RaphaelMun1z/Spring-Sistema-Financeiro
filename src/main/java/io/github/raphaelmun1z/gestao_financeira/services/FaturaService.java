package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class FaturaService {
    private final FaturaRepository repository;
    private final CartaoService cartaoService;

    public FaturaService(
        FaturaRepository repository,
        CartaoService cartaoService
    ) {
        this.repository = repository;
        this.cartaoService = cartaoService;
    }

    public Fatura cadastrar(Fatura data) {
        return repository.save(data);
    }

    public Fatura consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada!"));
    }

    public Fatura consultarEntidadePorCartaoEDataReferencia(String cartaoId, YearMonth data) {
        Optional<Fatura> fatura = repository.findByCartaoDeCredito_IdAndDataReferencia(cartaoId, data);
        if (fatura.isPresent()) return fatura.get();

        CartaoCredito cartaoCredito = (CartaoCredito) cartaoService.buscarEntidadePorId(cartaoId);
        return cadastrar(new Fatura(data, cartaoCredito));
    }

    @Transactional(readOnly = true)
    public FaturaResDTO consultarPorId(String id) {
        return new FaturaResDTO(consultarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<FaturaResDTO> consultarPorCartaoEPeriodo(String cartaoId, ConsultaPorPeriodoReqDTO data) {
        List<Fatura> faturas = repository.findByCartaoDeCredito_IdAndDataReferenciaBetween(cartaoId, YearMonth.from(data.dataInicial()), YearMonth.from(data.dataFinal()));
        return faturas.stream().map(FaturaResDTO::new).toList();
    }
}
