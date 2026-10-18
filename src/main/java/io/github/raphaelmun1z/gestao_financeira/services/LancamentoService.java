package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LancamentoService {
    private final LancamentoRepository repository;
    private final RegistroDeMovimentacaoRepository registroDeMovimentacaoRepository;
    private final FaturaService faturaService;

    public LancamentoService(
        LancamentoRepository repository,
        RegistroDeMovimentacaoRepository registroDeMovimentacaoRepository,
        FaturaService faturaService
    ) {
        this.repository = repository;
        this.registroDeMovimentacaoRepository = registroDeMovimentacaoRepository;
        this.faturaService = faturaService;
    }

    public LancamentoResDTO cadastrar(LancamentoReqDTO data) {
        Lancamento lancamentoObj = null;
        RegistroDeMovimentacao registroDeMovimentacao = registroDeMovimentacaoRepository.findById(data.registroDeMovimentacaoId())
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));

        if (data.faturaId() != null) {
            // Consulta fatura do próximo mês
            String cartaoId = registroDeMovimentacao.getCartao().getId();
            Fatura faturaCartao = faturaService.obterProximaFaturaDoCartao(cartaoId);

            lancamentoObj = new Lancamento(
                data.valor(),
                data.dataLancamento(),
                registroDeMovimentacao,
                faturaCartao
            );
        } else {
            lancamentoObj = new Lancamento(
                data.valor(),
                data.dataLancamento(),
                registroDeMovimentacao
            );
        }

        Lancamento lancamentoSalvo = repository.save(lancamentoObj);
        return new LancamentoResDTO(lancamentoSalvo);
    }

    private @NonNull LancamentoResDTO geraLancamentoFatura(LancamentoReqDTO data) {
        Fatura fatura = faturaService.consultarEntidadePorId(data.faturaId());

        Lancamento lancamento = new Lancamento(
            data.valor(),
            data.dataLancamento(),
            null,
            fatura
        );

        Lancamento lancamentoSalvo = repository.save(lancamento);
        return new LancamentoResDTO(lancamentoSalvo);
    }

    public List<LancamentoResDTO> consultarPorPeriodo(String contaBancariaId, ConsultaPorPeriodoReqDTO data) {
        List<Lancamento> lancamentos = repository.findByDataLancamentoBetween(data.dataInicial(), data.dataFinal());
        return lancamentos.stream().map(LancamentoResDTO::new).toList();
    }
}
