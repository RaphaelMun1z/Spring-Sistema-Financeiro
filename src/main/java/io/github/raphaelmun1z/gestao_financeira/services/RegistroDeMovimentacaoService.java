package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ConsultaPorPeriodoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.RegistroMovimentacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao.RegistroMovimentacaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistroDeMovimentacaoService {
    private final RegistroDeMovimentacaoRepository repository;
    private final CartaoService cartaoService;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;
    private final ContaBancariaService contaBancariaService;
    private final LancamentoService lancamentoService;

    public RegistroDeMovimentacaoService(
        RegistroDeMovimentacaoRepository repository,
        CartaoService cartaoService,
        CategoriaMovimentacaoService categoriaMovimentacaoService,
        ContaBancariaService contaBancariaService,
        LancamentoService lancamentoService
    ) {
        this.repository = repository;
        this.cartaoService = cartaoService;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
        this.contaBancariaService = contaBancariaService;
        this.lancamentoService = lancamentoService;
    }

    public RegistroMovimentacaoResDTO cadastrar(String contaBancariaId, RegistroMovimentacaoReqDTO data) {
        RegistroDeMovimentacao registroObj = null;
        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(contaBancariaId);
        CategoriaDeMovimentacao categoria = categoriaMovimentacaoService.buscarEntidadePorId(data.categoriaId());
        Cartao cartaoUtilizado = null;

        if (data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_CREDITO
            || data.metodoPagamento() == MetodoPagamentoEnum.CARTAO_DEBITO
            || data.metodoPagamento() == MetodoPagamentoEnum.VOUCHER) {
            cartaoUtilizado = cartaoService.buscarEntidadePorId(data.cartaoId());

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
        } else {
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
        }

        return new RegistroMovimentacaoResDTO(registroObj);
    }

    public RegistroDeMovimentacao consultarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Registro de movimentação não encontrado!"));
    }

    public List<RegistroMovimentacaoResDTO> consultarPorPeriodo(String contaBancariaId, ConsultaPorPeriodoReqDTO data) {
        List<RegistroDeMovimentacao> registros = repository.findByDataLancamentoBetween(data.dataInicial(), data.dataFinal());
        return registros.stream().map(RegistroMovimentacaoResDTO::new).toList();
    }
}
