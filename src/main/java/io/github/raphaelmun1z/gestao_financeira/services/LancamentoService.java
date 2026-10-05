package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.LancamentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.LancamentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.repositories.LancamentoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class LancamentoService {
    private final LancamentoRepository repository;
    private final ContaBancariaService contaBancariaService;
    private final CategoriaMovimentacaoService categoriaMovimentacaoService;
    private final CartaoService cartaoService;

    public LancamentoService(LancamentoRepository repository, ContaBancariaService contaBancariaService, CategoriaMovimentacaoService categoriaMovimentacaoService, CartaoService cartaoService) {
        this.repository = repository;
        this.contaBancariaService = contaBancariaService;
        this.categoriaMovimentacaoService = categoriaMovimentacaoService;
        this.cartaoService = cartaoService;
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
            data.metodoPagamento(),
            data.descricao(),
            data.dataLancamento(),
            data.valorTotal()
        );

        if (data.metodoPagamento() == MetodoPagamentoEnum.CARTAO) {
            Cartao cartao = cartaoService.buscarEntidadePorId(data.cartaoId());
            novoObj.setCartao(cartao);
        }

        return new LancamentoResDTO(repository.save(novoObj));
    }
}
