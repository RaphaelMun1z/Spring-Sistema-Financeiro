package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Validated
@Transactional(readOnly = true)
public class LancamentoService {
    private final LancamentoRepository repository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final FaturaService faturaService;
    private final ContaBancariaService contaBancariaService;

    public LancamentoService(
        LancamentoRepository repository,
        RegistroDeMovimentacaoRepository registroRepository,
        FaturaService faturaService,
        ContaBancariaService contaBancariaService
    ) {
        this.repository = repository;
        this.registroRepository = registroRepository;
        this.faturaService = faturaService;
        this.contaBancariaService = contaBancariaService;
    }

    @Transactional
    public LancamentoResDTO cadastrar(@Valid LancamentoReqDTO data) {
        return cadastrar(data, null);
    }

    @Transactional
    LancamentoResDTO cadastrar(@Valid LancamentoReqDTO data, Integer numeroParcela) {
        RegistroDeMovimentacao registro = registroRepository
            .findByIdAndContaBancaria_TitularId(data.registroDeMovimentacaoId(), UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
        contaBancariaService.buscarEntidadeParaAtualizar(registro.getContaBancaria().getId());
        boolean credito = registro.getMetodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO;
        if (credito != (data.faturaId() != null)) {
            throw new BusinessException("Somente lançamentos de crédito devem estar vinculados a uma fatura.");
        }
        Fatura fatura = data.faturaId() == null ? null : faturaService.consultarEntidadePorId(data.faturaId());
        if (fatura != null && (!fatura.getCartaoDeCredito().getId().equals(registro.getCartao().getId())
            || !fatura.getDataVencimento().equals(data.dataLancamento()))) {
            throw new BusinessException("A fatura ou a data não corresponde ao cartão e ao vencimento do lançamento.");
        }
        BigDecimal total = registro.getParcelas().stream().map(Lancamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (registro.getParcelas().size() >= registro.getQuantidadeParcelas()
            || total.add(data.valor()).compareTo(registro.getValorTotal()) > 0
            || registro.getParcelas().stream().anyMatch(item -> item.getDataLancamento().equals(data.dataLancamento()))) {
            throw new BusinessException("O lançamento excede o valor ou a quantidade de parcelas da movimentação.");
        }
        int parcela = numeroParcela == null ? registro.getParcelas().size() + 1 : numeroParcela;
        if (parcela < 1 || parcela > registro.getQuantidadeParcelas()
            || registro.getParcelas().stream().anyMatch(item -> parcela == item.getNumeroParcela())) {
            throw new BusinessException("O número da parcela é inválido para esta movimentação.");
        }
        Lancamento lancamento = repository.save(new Lancamento(data.valor(), data.dataLancamento(), registro, fatura, parcela));
        registro.getParcelas().add(lancamento);
        if (fatura != null) {
            fatura.getLancamentos().add(lancamento);
            fatura.recalcular();
        }
        return new LancamentoResDTO(lancamento);
    }

    void cadastrarAgendado(BigDecimal valor, java.time.LocalDate data, RegistroDeMovimentacao registro, Fatura fatura) {
        cadastrarAgendado(valor, data, registro, fatura, registro.getParcelas().size() + 1);
    }

    void cadastrarAgendado(BigDecimal valor, java.time.LocalDate data, RegistroDeMovimentacao registro,
                           Fatura fatura, int numeroParcela) {
        Lancamento lancamento = repository.save(new Lancamento(valor, data, registro, fatura, numeroParcela));
        registro.getParcelas().add(lancamento);
        if (fatura != null) {
            fatura.getLancamentos().add(lancamento);
            fatura.recalcular();
        }
    }

    @Transactional
    public void excluirPorRegistro(RegistroDeMovimentacao registro) {
        Set<Fatura> faturas = new HashSet<>();
        for (Lancamento lancamento : registro.getParcelas()) {
            Fatura fatura = lancamento.getFatura();
            if (fatura != null) {
                if (fatura.getStatus() == StatusFaturaEnum.PAGA) {
                    throw new BusinessException("Não é possível excluir uma movimentação com fatura paga.");
                }
                fatura.getLancamentos().remove(lancamento);
                faturas.add(fatura);
            }
        }
        registro.getParcelas().clear();
        faturas.forEach(Fatura::recalcular);
    }

    public List<LancamentoResDTO> consultarPorPeriodo(String contaBancariaId, @Valid ConsultaPorPeriodoReqDTO data) {
        contaBancariaService.buscarEntidadePorId(contaBancariaId);
        return repository.findByRegistroDeMovimentacao_ContaBancaria_IdAndDataLancamentoBetween(
            contaBancariaId, data.dataInicial(), data.dataFinal()
        ).stream().map(LancamentoResDTO::new).toList();
    }
}
