package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.MovimentacaoAtualizacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RecorrenciaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.*;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.SnapshotFinanceiroMensalRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.LocalDate;
import java.util.List;

@Service
@Validated
@Transactional(readOnly = true)
public class RegistroDeMovimentacaoService {
    private final RegistroDeMovimentacaoRepository repository;
    private final CartaoService cartaoService;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;
    private final ContaBancariaService contaBancariaService;
    private final LancamentoService lancamentoService;
    private final FaturaService faturaService;
    private final ContaBancariaRepository contaBancariaRepository;
    private final SnapshotFinanceiroMensalRepository snapshotRepository;

    public RegistroDeMovimentacaoService(
        RegistroDeMovimentacaoRepository repository,
        CartaoService cartaoService,
        CategoriaMovimentacaoService categoriaMovimentacaoService,
        ContaBancariaService contaBancariaService,
        LancamentoService lancamentoService,
        FaturaService faturaService,
        ContaBancariaRepository contaBancariaRepository,
        SnapshotFinanceiroMensalRepository snapshotRepository
    ) {
        this.repository = repository;
        this.cartaoService = cartaoService;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
        this.contaBancariaService = contaBancariaService;
        this.lancamentoService = lancamentoService;
        this.faturaService = faturaService;
        this.contaBancariaRepository = contaBancariaRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional
    public RegistroMovimentacaoResDTO cadastrar(String contaBancariaId, @Valid RegistroMovimentacaoReqDTO data) {
        // Todas as alterações financeiras da conta compartilham o mesmo bloqueio.
        ContaBancaria conta = contaBancariaService.buscarEntidadeParaAtualizar(contaBancariaId);
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(data.categoriaId());
        Cartao cartao = validarCartao(data, conta);
        boolean credito = data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO;
        int parcelas = data.quantidadeParcelas() == null ? 1 : data.quantidadeParcelas();

        if (credito) {
            if (data.quantidadeParcelas() == null) {
                throw new BusinessException("É necessário informar a quantidade de parcelas.");
            }
            if (categoria.getTipo() != TipoCategoriaEnum.SAIDA) {
                throw new BusinessException("Compras no crédito exigem uma categoria de saída.");
            }
            if (data.valorTotal().movePointRight(2).compareTo(BigDecimal.valueOf(parcelas)) < 0) {
                throw new BusinessException("Cada parcela deve ter pelo menos um centavo.");
            }
            CartaoCredito cartaoCredito = (CartaoCredito) cartao;
            BigDecimal disponivel = cartaoCredito.getLimiteCredito().subtract(faturaService.consultarCreditoUtilizado(cartao.getId()));
            if (disponivel.compareTo(data.valorTotal()) < 0 || conta.getCreditoRestante().compareTo(data.valorTotal()) < 0) {
                throw new BusinessException("Limite de crédito insuficiente.");
            }
            conta.setCreditoRestante(conta.getCreditoRestante().subtract(data.valorTotal()));
        } else {
            if (parcelas != 1) {
                throw new BusinessException("Somente compras no crédito podem ser parceladas.");
            }
            atualizarSaldo(conta, cartao, categoria, data.valorTotal(), false);
        }

        RegistroDeMovimentacao registro = repository.save(new RegistroDeMovimentacao(
            data.valorTotal(), data.dataLancamento(), data.descricao(), data.metodoPagamento(),
            data.ehRecorrente(), parcelas, categoria, conta, cartao
        ));

        if (credito) {
            CartaoCredito cartaoCredito = (CartaoCredito) cartao;
            YearMonth primeiraReferencia = cartaoCredito.calcularReferencia(data.dataLancamento());
            BigDecimal valorBase = data.valorTotal().divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.DOWN);
            int centavosRestantes = data.valorTotal().subtract(valorBase.multiply(BigDecimal.valueOf(parcelas)))
                .movePointRight(2).intValueExact();
            for (int mes = 0; mes < parcelas; mes++) {
                Fatura fatura = faturaService.consultarEntidadePorCartaoEDataReferencia(cartao.getId(), primeiraReferencia.plusMonths(mes));
                BigDecimal valor = mes < centavosRestantes ? valorBase.add(new BigDecimal("0.01")) : valorBase;
                lancamentoService.cadastrar(new LancamentoReqDTO(valor, fatura.getDataVencimento(), registro.getId(), fatura.getId()), mes + 1);
            }
        } else {
            lancamentoService.cadastrar(new LancamentoReqDTO(data.valorTotal(), data.dataLancamento(), registro.getId(), null));
        }
        return new RegistroMovimentacaoResDTO(registro);
    }

    private Cartao validarCartao(RegistroMovimentacaoReqDTO data, ContaBancaria conta) {
        boolean exigeCartao = switch (data.metodoPagamento()) {
            case CARTAO_CREDITO, CARTAO_DEBITO, VOUCHER -> true;
            default -> false;
        };
        if (!exigeCartao) {
            if (data.cartaoId() != null && !data.cartaoId().isBlank()) {
                throw new BusinessException("O método informado não utiliza cartão.");
            }
            return null;
        }
        if (data.cartaoId() == null || data.cartaoId().isBlank()) {
            throw new BusinessException("O cartão é obrigatório para este método de pagamento.");
        }
        Cartao cartao = cartaoService.buscarEntidadePorId(data.cartaoId());
        if (!conta.getId().equals(cartao.getContaBancariaId())) {
            throw new BusinessException("O cartão não pertence à conta informada.");
        }
        boolean compativel = switch (data.metodoPagamento()) {
            case CARTAO_CREDITO -> cartao instanceof CartaoCredito;
            case CARTAO_DEBITO -> cartao instanceof CartaoDebito;
            case VOUCHER -> cartao instanceof CartaoVoucher;
            default -> false;
        };
        if (!compativel) {
            throw new BusinessException("O cartão é incompatível com o método de pagamento.");
        }
        return cartao;
    }

    private void atualizarSaldo(ContaBancaria conta, Cartao cartao, CategoriaDeMovimentacao categoria, BigDecimal valor, boolean estorno) {
        BigDecimal variacao = categoria.getTipo() == TipoCategoriaEnum.ENTRADA ? valor : valor.negate();
        if (estorno) {
            variacao = variacao.negate();
        }
        BigDecimal saldo = cartao instanceof CartaoVoucher voucher ? voucher.getSaldoCorrente() : conta.getSaldoCorrente();
        BigDecimal novoSaldo = saldo.add(variacao);
        if (novoSaldo.signum() < 0) {
            throw new BusinessException("Saldo insuficiente para realizar a operação.");
        }
        if (cartao instanceof CartaoVoucher voucher) {
            voucher.setSaldoCorrente(novoSaldo);
        } else {
            conta.setSaldoCorrente(novoSaldo);
        }
    }

    @Transactional
    public void excluir(String id) {
        String contaId = repository.buscarContaBancariaId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
        ContaBancaria conta = contaBancariaService.buscarEntidadeParaAtualizar(contaId);
        RegistroDeMovimentacao registro = consultarEntidadePorId(id);
        if (snapshotRepository.existsByContaBancaria_IdAndMesReferencia(
            contaId, YearMonth.from(registro.getDataLancamento()))) {
            throw new BusinessException("Não é possível excluir uma movimentação de um mês já fechado.");
        }
        lancamentoService.excluirPorRegistro(registro);
        if (registro.getMetodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO) {
            conta.setCreditoRestante(conta.getCreditoRestante().add(registro.getValorTotal()));
        } else {
            atualizarSaldo(conta, registro.getCartao(), registro.getCategoria(), registro.getValorTotal(), true);
        }
        repository.delete(registro);
    }

    public RegistroDeMovimentacao consultarEntidadePorId(String id) {
        return repository.findByIdAndContaBancaria_TitularId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
    }

    public RegistroMovimentacaoResDTO consultarPorId(String id) {
        return new RegistroMovimentacaoResDTO(consultarEntidadePorId(id));
    }

    public List<RegistroMovimentacaoResDTO> consultarPorContaBancariaEPeriodo(String contaBancariaId, @Valid ConsultaPorPeriodoReqDTO data) {
        contaBancariaService.buscarEntidadePorId(contaBancariaId);
        return repository.findByContaBancaria_IdAndDataLancamentoBetween(contaBancariaId, data.dataInicial(), data.dataFinal())
            .stream().map(RegistroMovimentacaoResDTO::new).toList();
    }

    @Transactional
    public RegistroMovimentacaoResDTO atualizar(String id, MovimentacaoAtualizacaoReqDTO data) {
        RegistroDeMovimentacao registro = consultarEntidadePorId(id);
        registro.setDescricao(data.descricao());
        return new RegistroMovimentacaoResDTO(registro);
    }

    @Transactional
    public RegistroMovimentacaoResDTO atualizarRecorrencia(String id, RecorrenciaReqDTO data) {
        RegistroDeMovimentacao registro = consultarEntidadePorId(id);
        registro.setRecorrente(data.ativa());
        return new RegistroMovimentacaoResDTO(registro);
    }

    @Transactional
    public void processarRecorrencias(LocalDate hoje) {
        for (RegistroDeMovimentacao origem : repository.buscarRecorrenciasPendentes(hoje)) {
            while (!origem.getProximaRecorrencia().isAfter(hoje)) {
                try {
                    gerarRecorrencia(origem, origem.getProximaRecorrencia());
                } catch (BusinessException ex) {
                    break;
                }
                origem.avancarRecorrencia();
            }
        }
    }

    private void gerarRecorrencia(RegistroDeMovimentacao origem, LocalDate data) {
        ContaBancaria conta = contaBancariaRepository.buscarPorIdParaAgendamento(origem.getContaBancaria().getId())
            .orElseThrow(() -> new NotFoundException("Conta bancária da recorrência não encontrada."));
        Cartao cartao = origem.getCartao();
        boolean credito = origem.getMetodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO;
        if (credito) {
            CartaoCredito cartaoCredito = (CartaoCredito) cartao;
            BigDecimal utilizado = faturaService.consultarCreditoUtilizadoAgendado(cartaoCredito.getId());
            BigDecimal disponivel = cartaoCredito.getLimiteCredito().subtract(utilizado);
            if (disponivel.compareTo(origem.getValorTotal()) < 0
                || conta.getCreditoRestante().compareTo(origem.getValorTotal()) < 0) {
                throw new BusinessException("Crédito insuficiente para gerar a recorrência.");
            }
            conta.setCreditoRestante(conta.getCreditoRestante().subtract(origem.getValorTotal()));
        } else {
            atualizarSaldo(conta, cartao, origem.getCategoria(), origem.getValorTotal(), false);
        }

        RegistroDeMovimentacao nova = repository.save(new RegistroDeMovimentacao(
            origem.getValorTotal(), data, origem.getDescricao(), origem.getMetodoPagamento(),
            false, origem.getQuantidadeParcelas(), origem.getCategoria(), conta, cartao));

        if (credito) {
            CartaoCredito cartaoCredito = (CartaoCredito) cartao;
            YearMonth primeiraReferencia = cartaoCredito.calcularReferencia(data);
            BigDecimal valorBase = origem.getValorTotal().divide(BigDecimal.valueOf(origem.getQuantidadeParcelas()), 2, RoundingMode.DOWN);
            int centavosRestantes = origem.getValorTotal()
                .subtract(valorBase.multiply(BigDecimal.valueOf(origem.getQuantidadeParcelas())))
                .movePointRight(2).intValueExact();
            for (int mes = 0; mes < origem.getQuantidadeParcelas(); mes++) {
                Fatura fatura = faturaService.consultarOuCriarFatura(cartaoCredito, primeiraReferencia.plusMonths(mes));
                BigDecimal valor = mes < centavosRestantes ? valorBase.add(new BigDecimal("0.01")) : valorBase;
                lancamentoService.cadastrarAgendado(valor, fatura.getDataVencimento(), nova, fatura, mes + 1);
            }
        } else {
            lancamentoService.cadastrarAgendado(origem.getValorTotal(), data, nova, null);
        }
    }
}
