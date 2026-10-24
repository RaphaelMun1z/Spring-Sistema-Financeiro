package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.FaturaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.PagamentoFatura;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.PagamentoFaturaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@Validated
@Transactional
public class FaturaService {
    private final FaturaRepository repository;
    private final CartaoService cartaoService;
    private final ContaBancariaService contaBancariaService;
    private final PagamentoFaturaRepository pagamentoRepository;

    public FaturaService(FaturaRepository repository, CartaoService cartaoService, ContaBancariaService contaBancariaService,
                         PagamentoFaturaRepository pagamentoRepository) {
        this.repository = repository;
        this.cartaoService = cartaoService;
        this.contaBancariaService = contaBancariaService;
        this.pagamentoRepository = pagamentoRepository;
    }

    public Fatura consultarEntidadePorId(String id) {
        return repository.findByIdAndCartaoDeCredito_ContaBancaria_TitularId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada!"));
    }

    private CartaoCredito buscarCartaoCredito(String id) {
        Cartao cartao = cartaoService.buscarEntidadePorId(id);
        if (!(cartao instanceof CartaoCredito credito)) {
            throw new BusinessException("A consulta de faturas exige um cartão de crédito.");
        }
        return credito;
    }

    public Fatura consultarEntidadePorCartaoEDataReferencia(String cartaoId, YearMonth data) {
        CartaoCredito cartao = buscarCartaoCredito(cartaoId);
        contaBancariaService.buscarEntidadeParaAtualizar(cartao.getContaBancariaId());
        return consultarOuCriarFatura(cartao, data);
    }

    Fatura consultarOuCriarFatura(CartaoCredito cartao, YearMonth data) {
        return repository.findByCartaoDeCredito_IdAndDataReferencia(cartao.getId(), data)
            .orElseGet(() -> repository.save(new Fatura(data, cartao)));
    }

    BigDecimal consultarCreditoUtilizadoAgendado(String cartaoId) {
        return repository.somarTotalPorCartaoEStatusDiferente(cartaoId, StatusFaturaEnum.PAGA);
    }

    public BigDecimal consultarCreditoUtilizado(String cartaoId) {
        buscarCartaoCredito(cartaoId);
        return repository.somarTotalPorCartaoEStatusDiferente(cartaoId, StatusFaturaEnum.PAGA);
    }

    public FaturaResDTO consultarPorId(String id) {
        bloquearContaDaFatura(id);
        Fatura fatura = consultarEntidadePorId(id);
        fatura.atualizarStatus();
        return new FaturaResDTO(fatura);
    }

    public FaturaResDTO consultarPorCaminho(String contaBancariaId, String cartaoId, String faturaId) {
        contaBancariaService.buscarEntidadeParaAtualizar(contaBancariaId);
        Fatura fatura = repository
            .findByIdAndCartaoDeCredito_IdAndCartaoDeCredito_ContaBancaria_IdAndCartaoDeCredito_ContaBancaria_TitularId(
                faturaId, cartaoId, contaBancariaId, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada nesse cartão e conta!"));
        fatura.atualizarStatus();
        return new FaturaResDTO(fatura);
    }

    public List<FaturaResDTO> consultarPorCartaoEPeriodo(String cartaoId, @Valid ConsultaPorPeriodoReqDTO data) {
        CartaoCredito cartao = buscarCartaoCredito(cartaoId);
        contaBancariaService.buscarEntidadeParaAtualizar(cartao.getContaBancariaId());
        List<Fatura> faturas = repository.findByCartaoDeCredito_IdAndDataReferenciaBetween(
            cartaoId, YearMonth.from(data.dataInicial()), YearMonth.from(data.dataFinal()));
        faturas.forEach(Fatura::atualizarStatus);
        return faturas.stream().sorted(java.util.Comparator.comparing(Fatura::getDataReferencia)).map(FaturaResDTO::new).toList();
    }

    public FaturaResDTO pagar(String id) {
        ContaBancaria conta = bloquearContaDaFatura(id);
        Fatura fatura = consultarEntidadePorId(id);
        if (fatura.getStatus() == StatusFaturaEnum.PAGA) {
            return new FaturaResDTO(fatura);
        }
        if (conta.getSaldoCorrente().compareTo(fatura.getValorTotal()) < 0) {
            throw new BusinessException("Saldo insuficiente para pagar a fatura.");
        }
        fatura.pagar();
        conta.setSaldoCorrente(conta.getSaldoCorrente().subtract(fatura.getValorTotal()));
        conta.setCreditoRestante(conta.getCreditoRestante().add(fatura.getValorTotal()));
        pagamentoRepository.save(new PagamentoFatura(fatura, conta, fatura.getValorTotal(), java.time.LocalDate.now()));
        return new FaturaResDTO(fatura);
    }

    private ContaBancaria bloquearContaDaFatura(String id) {
        String contaId = repository.buscarContaBancariaId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Fatura não encontrada!"));
        return contaBancariaService.buscarEntidadeParaAtualizar(contaId);
    }
}
