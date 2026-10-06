package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ParcelaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoPadrao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;

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
        if ((data.metodoPagamento() == MetodoPagamentoEnum.CARTAO) && (data.cartaoId() == null)) {
            throw new BusinessException("É necessário informar o 'cartaoId' para registrar um lançamento com cartão!");
        }

        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(data.contaBancariaId());
        CategoriaDeMovimentacao categoriaDeMovimentacao = categoriaMovimentacaoService.buscarEntidadePorId(data.categoriaId());
        Lancamento novoObj = new Lancamento(
            contaBancaria,
            categoriaDeMovimentacao,
            data.ehRecorrente(),
            data.quantidadeParcelas(),
            data.metodoPagamento(),
            data.descricao(),
            data.dataLancamento(),
            data.valorTotal()
        );

        Cartao cartao = null;
        if (data.metodoPagamento() == MetodoPagamentoEnum.CARTAO) {
            cartao = cartaoService.buscarEntidadePorId(data.cartaoId());
            novoObj.setCartao(cartao);
        }

        Lancamento salvo = repository.save(novoObj);

        // Gera parcelas
        if (data.metodoPagamento() == MetodoPagamentoEnum.CARTAO && data.quantidadeParcelas() > 0) {
            Integer diaFechamentoFatura = ((CartaoPadrao) cartao).getDiaFechamento();
            LocalDate dataLancamento = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), diaFechamentoFatura);
            BigDecimal valorParcela = data.valorTotal().divide(BigDecimal.valueOf(data.quantidadeParcelas()), RoundingMode.CEILING);
            Fatura fatura = faturaService.cadastrar(
                new Fatura(
                    BigDecimal.valueOf(1000),
                    YearMonth.now()
                )
            );

            for (int ii = 0; ii < data.quantidadeParcelas(); ii++) {
                parcelaService.cadastrar(new ParcelaReqDTO(
                    valorParcela,
                    salvo.getId(),
                    dataLancamento,
                    fatura.getId()
                ));
                dataLancamento = dataLancamento.plusMonths(1);
            }
        }

        return new LancamentoResDTO(salvo);
    }

    public Lancamento buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Lançamento não encontrado!"));
    }
}
