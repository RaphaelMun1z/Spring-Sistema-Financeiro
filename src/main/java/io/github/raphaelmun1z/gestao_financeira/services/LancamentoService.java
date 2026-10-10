package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ParcelaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.*;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoPadraoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.enums.TipoCartaoVouncherEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class LancamentoService {
    private final LancamentoRepository repository;
    private final ContaBancariaService contaBancariaService;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;
    private final CartaoService cartaoService;
    private final ParcelaService parcelaService;
    private final FaturaService faturaService;

    public LancamentoService(LancamentoRepository repository, ContaBancariaService contaBancariaService, CategoriaMovimentacaoService categoriaMovimentacaoService, CartaoService cartaoService, ParcelaService parcelaService, FaturaService faturaService) {
        this.repository = repository;
        this.contaBancariaService = contaBancariaService;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
        this.cartaoService = cartaoService;
        this.parcelaService = parcelaService;
        this.faturaService = faturaService;
    }

    public LancamentoResDTO cadastrar(LancamentoReqDTO data) {
        validaDadosCartao(data);

        Lancamento lancamento = geraLancamento(data);

        Cartao cartao = defineCartao(data, lancamento);

        Lancamento lancamentoSalvo = repository.save(lancamento);

        int quantidadeParcelas = data.quantidadeParcelas() != null ? data.quantidadeParcelas() : 1;

        switch (data.metodoPagamento()) {
            case CARTAO_DEBITO -> pagarComCartaoDeDebito(data.valorTotal(), lancamentoSalvo.getId(), cartao);
            case CARTAO_CREDITO -> pagarComCartaoDeCredito(data.valorTotal(), lancamentoSalvo.getId(), cartao, quantidadeParcelas);
            case VOUNCHER -> pagarComVouncher(data.valorTotal(), lancamentoSalvo.getId(), cartao);
            default -> pagarOutrosMetodos(data.valorTotal(), lancamentoSalvo.getId());
        }

        return new LancamentoResDTO(lancamentoSalvo);
    }

    private @Nullable Cartao defineCartao(LancamentoReqDTO data, Lancamento lancamento) {
        Cartao cartao = null;
        if (data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO
        || data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_DEBITO) {
            cartao = cartaoService.buscarEntidadePorId(data.cartaoId());
            lancamento.setCartao(cartao);
        }
        return cartao;
    }

    private @NonNull Lancamento geraLancamento(LancamentoReqDTO data) {
        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(data.contaBancariaId());
        CategoriaDeMovimentacao categoriaDeMovimentacao = categoriaMovimentacaoService.buscarEntidadePorId(data.categoriaId());
        return new Lancamento(
            contaBancaria,
            categoriaDeMovimentacao,
            data.ehRecorrente(),
            data.quantidadeParcelas(),
            data.metodoPagamento(),
            data.descricao(),
            data.dataLancamento(),
            data.valorTotal()
        );
    }

    private static void validaDadosCartao(LancamentoReqDTO data) {
        if ((data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO
            || data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_DEBITO)
            && (data.cartaoId() == null)) {
            throw new BusinessException("É necessário informar o 'cartaoId' para registrar um lançamento com cartão!");
        }
    }

    private void pagarComCartaoDeDebito(BigDecimal valorTotal, String lancamentoId, Cartao cartao) {
        if (!(cartao instanceof CartaoPadrao cartaoDebito)
            || (cartaoDebito.getTipoCartao() != TipoCartaoPadraoEnum.DEBITO
            && cartaoDebito.getTipoCartao() != TipoCartaoPadraoEnum.CREDITO_E_DEBITO)) {
            throw new BusinessException("O cartão selecionado não é de Débito!");
        }

        // Registrar parcela única no dia da compra
        parcelaService.cadastrar(new ParcelaReqDTO(
            valorTotal,
            lancamentoId
        ));
    }

    private void pagarComCartaoDeCredito(BigDecimal valorTotal, String lancamentoId, Cartao cartao, int qntParcelas) {
        if (!(cartao instanceof CartaoPadrao cartaoCredito)
            || (cartaoCredito.getTipoCartao() != TipoCartaoPadraoEnum.CREDITO
            && cartaoCredito.getTipoCartao() != TipoCartaoPadraoEnum.CREDITO_E_DEBITO)) {
            throw new BusinessException("O cartão selecionado não é de Crédito!");
        }

        // Obtém dados do cartão
        int diaFechamentoFatura = cartaoCredito.getDiaFechamento();
        BigDecimal valorParcela = valorTotal.divide(BigDecimal.valueOf(qntParcelas), RoundingMode.CEILING);

        LocalDate dataLancamentoParcela;
        for (int mes = 1; mes <= qntParcelas; mes++) {
            dataLancamentoParcela = LocalDate.now().plusMonths(mes).withDayOfMonth(diaFechamentoFatura + 1);

            // Busca fatura do mês referência
            // TO-DO: busca fatura real
            Fatura faturaDoMes = new Fatura();

            parcelaService.cadastrar(new ParcelaReqDTO(
                valorParcela,
                lancamentoId,
                dataLancamentoParcela,
                faturaDoMes.getId()
            ));

            // A cada parcela incrementa um mês
            dataLancamentoParcela.plusMonths(1);
        }
    }

    private void pagarComVouncher(BigDecimal valorTotal, String lancamentoId, Cartao cartao){
        if (!(cartao instanceof CartaoVouncher vouncher)) {
            throw new BusinessException("O cartão selecionado não é Vouncher!");
        }

        // TO-DO: referenciar historico real
        HistoricoVouncher historicoVouncher = new HistoricoVouncher();

        // Registrar parcela única no dia da compra
        parcelaService.cadastrar(new ParcelaReqDTO(
            valorTotal,
            lancamentoId,
            LocalDate.now(),
            historicoVouncher.getId()
        ));
    }

    private void pagarOutrosMetodos(BigDecimal valorTotal, String lancamentoId) {
        // Registrar parcela única no dia da compra
        parcelaService.cadastrar(new ParcelaReqDTO(
            valorTotal,
            lancamentoId
        ));
    }

    public Lancamento buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Lançamento não encontrado!"));
    }
}
