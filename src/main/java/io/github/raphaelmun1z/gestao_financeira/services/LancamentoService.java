package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaExtratoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LancamentoService {
    private final LancamentoRepository repository;
    private final RegistroDeMovimentacaoService registroDeMovimentacaoService;
    private final FaturaService faturaService;

    public LancamentoService(
        LancamentoRepository repository,
        RegistroDeMovimentacaoService registroDeMovimentacaoService,
        FaturaService faturaService
    ) {
        this.repository = repository;
        this.registroDeMovimentacaoService = registroDeMovimentacaoService;
        this.faturaService = faturaService;
    }

    public LancamentoResDTO cadastrar(LancamentoReqDTO data) {
        validarDados(data);

        if (data.registroDeMovimentacaoId() != null) {
            return geraLancamentoMovimentacao(data);
        }

        if (data.faturaId() != null) {
            return geraLancamentoFatura(data);
        }

        throw new BusinessException("Não foram passados os dados necessários para o cadastro do lançamento.");
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

    private @NonNull LancamentoResDTO geraLancamentoMovimentacao(LancamentoReqDTO data) {
        RegistroDeMovimentacao registroDeMovimentacao = registroDeMovimentacaoService.consultarEntidadePorId(data.registroDeMovimentacaoId());

        Lancamento lancamento = new Lancamento(
            data.valor(),
            data.dataLancamento(),
            registroDeMovimentacao,
            null
        );

        Lancamento lancamentoSalvo = repository.save(lancamento);
        return new LancamentoResDTO(lancamentoSalvo);
    }

    private static void validarDados(LancamentoReqDTO data) {
        if (data.registroDeMovimentacaoId() == null && data.faturaId() == null) {
            throw new IllegalArgumentException("É necessário vincular o lançamento à um registro de movimentação ou à uma fatura.");
        }

        if (data.registroDeMovimentacaoId() != null && data.faturaId() != null) {
            throw new IllegalArgumentException("O lançamento deve estar associado a apenas uma das opções - registro de movimentação OU fatura.");
        }
    }

    public List<LancamentoResDTO> consultarExtratoPorPeriodo(ConsultaExtratoReqDTO data) {
        List<Lancamento> lancamentos = repository.findByDataLancamentoBetween(data.dataInicial(), data.dataFinal());
        return lancamentos.stream().map(LancamentoResDTO::new).toList();
    }
}
