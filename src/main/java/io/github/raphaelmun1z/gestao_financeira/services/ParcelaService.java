package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ParcelaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ParcelaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.RegistroDeDespesas;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.ParcelaRepository;
import org.springframework.stereotype.Service;

@Service
public class ParcelaService {
    private final ParcelaRepository repository;
    private final LancamentoRepository lancamentoRepository;
    private final RegistroDeDespesasService registroDeDespesasService;

    public ParcelaService(ParcelaRepository repository, LancamentoRepository lancamentoRepository, RegistroDeDespesasService registroDeDespesasService) {
        this.repository = repository;
        this.lancamentoRepository = lancamentoRepository;
        this.registroDeDespesasService = registroDeDespesasService;
    }

    public ParcelaResDTO cadastrar(ParcelaReqDTO data) {
        Lancamento lancamento = lancamentoRepository.findById(data.lancamentoId()).orElseThrow(() -> new NotFoundException("Lançamento não encontrado!"));
        RegistroDeDespesas registro = null;
        if(data.registroDeDespesasId() != null)
            registro = registroDeDespesasService.buscarEntidadePorId(data.registroDeDespesasId());

        Parcela parcela = new Parcela(
            data.valorParcela(),
            data.dataLancamento(),
            lancamento,
            registro
        );

        return new ParcelaResDTO(repository.save(parcela));
    }
}