package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class RegistroDeMovimentacaoService {
    private final RegistroDeMovimentacaoRepository repository;
    private final CartaoService cartaoService;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;
    private final ContaBancariaService contaBancariaService;
    private final LancamentoService lancamentoService;
    private final FaturaService faturaService;

    public RegistroDeMovimentacaoService(
        RegistroDeMovimentacaoRepository repository,
        CartaoService cartaoService,
        CategoriaMovimentacaoService categoriaMovimentacaoService,
        ContaBancariaService contaBancariaService,
        LancamentoService lancamentoService,
        FaturaService faturaService
    ) {
        this.repository = repository;
        this.cartaoService = cartaoService;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
        this.contaBancariaService = contaBancariaService;
        this.lancamentoService = lancamentoService;
        this.faturaService = faturaService;
    }

    public RegistroMovimentacaoResDTO cadastrar(String contaBancariaId, RegistroMovimentacaoReqDTO data) {
        RegistroDeMovimentacao registroObj = null;
        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(contaBancariaId);
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(data.categoriaId());

        if (metodoPagamentoEhCartao(data)) {
            registroObj = gerarMovimentacaoComCartao(data, categoria, contaBancaria);
        } else {
            registroObj = gerarMovimentacaoSemCartao(data, categoria, contaBancaria);
        }

        return new RegistroMovimentacaoResDTO(registroObj);
    }

    private static boolean metodoPagamentoEhCartao(RegistroMovimentacaoReqDTO data) {
        return data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO
            || data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_DEBITO
            || data.metodoPagamento() == MetodoPagamentoEnum.VOUCHER;
    }

    private @NonNull RegistroDeMovimentacao gerarMovimentacaoComCartao(RegistroMovimentacaoReqDTO data, CategoriaDeMovimentacao categoria, ContaBancaria contaBancaria) {
        Cartao cartaoUtilizado = cartaoService.buscarEntidadePorId(data.cartaoId());

        return switch (data.metodoPagamento()) {
            case CARTAO_CREDITO -> movimentacaoCredito(data, categoria, contaBancaria, cartaoUtilizado);
            case CARTAO_DEBITO -> movimentacaoDebito(data, categoria, contaBancaria, cartaoUtilizado);
            case VOUCHER -> movimentacaoVoucher(data, categoria, contaBancaria, cartaoUtilizado);
            default -> throw new BusinessException("Ocorreu um erro inesperado.");
        };
    }

    private @NonNull RegistroDeMovimentacao movimentacaoCredito(RegistroMovimentacaoReqDTO data, CategoriaDeMovimentacao categoria, ContaBancaria contaBancaria, Cartao cartaoUtilizado) {
        RegistroDeMovimentacao registroObj;
        if (data.quantidadeParcelas() == null)
            throw new BusinessException("É necessário informar a quantidade de parcelas.");

        registroObj = new RegistroDeMovimentacao(
            data.valorTotal(),
            data.dataLancamento(),
            data.descricao(),
            data.metodoPagamento(),
            data.ehRecorrente(),
            data.quantidadeParcelas(),
            categoria,
            contaBancaria,
            cartaoUtilizado
        );

        registroObj = repository.save(registroObj);

        BigDecimal valorParcela = data.valorTotal().divide(new BigDecimal(data.quantidadeParcelas()), RoundingMode.CEILING);
        Integer diaFechamentoFatura = ((CartaoCredito) cartaoUtilizado).getDiaFechamento();
        LocalDate dataLancamento;
        Fatura faturaDoMes = null;
        for (int mes = 1; mes <= data.quantidadeParcelas(); mes++) {
            dataLancamento = LocalDate.now().plusMonths(mes).withDayOfMonth(diaFechamentoFatura);
            faturaDoMes = faturaService.consultarEntidadePorCartaoEDataReferencia(cartaoUtilizado.getId(), YearMonth.from(dataLancamento));

            LancamentoReqDTO lancamentoReqObj = new LancamentoReqDTO(
                valorParcela,
                dataLancamento,
                registroObj.getId(),
                faturaDoMes.getId()
            );

            lancamentoService.cadastrar(lancamentoReqObj);
        }

        return registroObj;
    }

    private @NonNull RegistroDeMovimentacao movimentacaoVoucher(RegistroMovimentacaoReqDTO data, CategoriaDeMovimentacao categoria, ContaBancaria contaBancaria, Cartao cartaoUtilizado) {
        RegistroDeMovimentacao registroObj;
        registroObj = new RegistroDeMovimentacao(
            data.valorTotal(),
            data.dataLancamento(),
            data.descricao(),
            data.metodoPagamento(),
            data.ehRecorrente(),
            1,
            categoria,
            contaBancaria,
            cartaoUtilizado
        );

        registroObj = repository.save(registroObj);

        LancamentoReqDTO lancamentoReqObj = new LancamentoReqDTO(
            registroObj.getValorTotal(),
            registroObj.getDataLancamento(),
            registroObj.getId(),
            null
        );

        lancamentoService.cadastrar(lancamentoReqObj);
        return registroObj;
    }

    private @NonNull RegistroDeMovimentacao movimentacaoDebito(RegistroMovimentacaoReqDTO data, CategoriaDeMovimentacao categoria, ContaBancaria contaBancaria, Cartao cartaoUtilizado) {
        RegistroDeMovimentacao registroObj;
        registroObj = new RegistroDeMovimentacao(
            data.valorTotal(),
            data.dataLancamento(),
            data.descricao(),
            data.metodoPagamento(),
            data.ehRecorrente(),
            1,
            categoria,
            contaBancaria,
            cartaoUtilizado
        );

        registroObj = repository.save(registroObj);

        LancamentoReqDTO lancamentoReqObj = new LancamentoReqDTO(
            registroObj.getValorTotal(),
            registroObj.getDataLancamento(),
            registroObj.getId(),
            null
        );

        lancamentoService.cadastrar(lancamentoReqObj);
        return registroObj;
    }

    private @NonNull RegistroDeMovimentacao gerarMovimentacaoSemCartao(RegistroMovimentacaoReqDTO data, CategoriaDeMovimentacao categoria, ContaBancaria contaBancaria) {
        RegistroDeMovimentacao registroObj;
        registroObj = new RegistroDeMovimentacao(
            data.valorTotal(),
            data.dataLancamento(),
            data.descricao(),
            data.metodoPagamento(),
            data.ehRecorrente(),
            categoria,
            contaBancaria
        );

        registroObj = repository.save(registroObj);

        LancamentoReqDTO lancamentoReqObj = new LancamentoReqDTO(
            registroObj.getValorTotal(),
            registroObj.getDataLancamento(),
            registroObj.getId(),
            null
        );

        lancamentoService.cadastrar(lancamentoReqObj);
        return registroObj;
    }

    public RegistroDeMovimentacao consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
    }

    public RegistroMovimentacaoResDTO consultarPorId(String id) {
        return new RegistroMovimentacaoResDTO(consultarEntidadePorId(id));
    }

    public List<RegistroMovimentacaoResDTO> consultarPorContaBancariaEPeriodo(String contaBancariaId, ConsultaPorPeriodoReqDTO data) {
        List<RegistroDeMovimentacao> registros = repository.findByContaBancaria_IdAndDataLancamentoBetween(contaBancariaId, data.dataInicial(), data.dataFinal());
        return registros.stream().map(RegistroMovimentacaoResDTO::new).toList();
    }
}
