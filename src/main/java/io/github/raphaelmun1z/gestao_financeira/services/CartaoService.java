package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoCreditoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoDebitoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.cartao.CartaoVoucherResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCartaoEnum;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartaoService {
    private final CartaoRepository repository;
    private final ContaBancariaService contaBancariaService;
    private final FaturaRepository faturaRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;

    public CartaoService(CartaoRepository cartaoRepository, ContaBancariaService contaBancariaService, FaturaRepository faturaRepository, RegistroDeMovimentacaoRepository registroRepository) {
        this.repository = cartaoRepository;
        this.contaBancariaService = contaBancariaService;
        this.faturaRepository = faturaRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public CartaoResDTO cadastrar(CartaoReqDTO data) {
        validarTipoDoCartao(data);

        // Define a conta bancária
        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(data.contaBancariaId());

        switch (data.tipoCartao()) {
            case CREDITO -> {
                return cadastrarCartaoDeCredito(contaBancaria, data);
            }
            case DEBITO -> {
                return cadastrarCartaoDeDebito(contaBancaria, data);
            }
            case VOUCHER -> {
                return cadastrarVoucher(contaBancaria, data);
            }
            default -> throw new IllegalArgumentException("O tipo de cartão informado é inválido!");
        }
    }

    private static void validarTipoDoCartao(CartaoReqDTO data) {
        switch (data.tipoCartao()) {
            case CREDITO -> {
                if (data.cartaoCredito() == null || data.cartaoVoucher() != null) {
                    throw new IllegalArgumentException("Dados de cartão de crédito inválidos. Para registrar um cartão de crédito, você não deve preencher dados relativos a outros tipos de cartão.");
                }
            }

            case VOUCHER -> {
                if (data.cartaoVoucher() == null || data.cartaoCredito() != null) {
                    throw new IllegalArgumentException("Dados de cartão voucher inválidos. Para registrar um cartão voucher, você não deve preencher dados relativos a outros tipos de cartão.");
                }
            }

            case DEBITO -> {
                if (data.cartaoCredito() != null || data.cartaoVoucher() != null) {
                    throw new IllegalArgumentException("Para registrar um cartão de débito, você não deve preencher dados relativos a outros tipos de cartão.");
                }
            }
        }
    }

    private CartaoResDTO cadastrarCartaoDeCredito(ContaBancaria contaBancaria, CartaoReqDTO data) {
        CartaoCredito cc = new CartaoCredito(
            data.apelido(),
            contaBancaria,
            data.cartaoCredito().limiteCredito(),
            data.cartaoCredito().diaFechamento(),
            data.cartaoCredito().diaVencimento()
        );
        return new CartaoCreditoResDTO(repository.save(cc));
    }

    private CartaoResDTO cadastrarCartaoDeDebito(ContaBancaria contaBancaria, CartaoReqDTO data) {
        CartaoDebito cd = new CartaoDebito(
            data.apelido(),
            contaBancaria
        );
        return new CartaoDebitoResDTO(repository.save(cd));
    }

    private CartaoResDTO cadastrarVoucher(ContaBancaria contaBancaria, CartaoReqDTO data) {
        CartaoVoucher cv = new CartaoVoucher(
            data.apelido(),
            contaBancaria,
            data.cartaoVoucher().diaRecarga(),
            data.cartaoVoucher().valorRecarga(),
            data.cartaoVoucher().saldoCorrente(),
            data.cartaoVoucher().tipoVoucher()
        );
        return new CartaoVoucherResDTO(repository.save(cv));
    }

    public Cartao buscarEntidadePorId(String id) {
        return repository.findByIdAndContaBancaria_TitularId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado!"));
    }

    public List<CartaoResDTO> consultarPorContaBancaria(String contaBancariaId) {
        contaBancariaService.buscarEntidadePorId(contaBancariaId);
        return repository.findByContaBancaria_Id(contaBancariaId).stream()
            .<CartaoResDTO>map(cartao -> switch (cartao) {
                case CartaoCredito cc -> new CartaoCreditoResDTO(cc);
                case CartaoDebito cd -> new CartaoDebitoResDTO(cd);
                case CartaoVoucher cv -> new CartaoVoucherResDTO(cv);
                default -> throw new IllegalStateException("Tipo de cartão desconhecido.");
            }).toList();
    }

    public CartaoResDTO consultarPorContaEId(String contaBancariaId, String cartaoId) {
        contaBancariaService.buscarEntidadePorId(contaBancariaId);
        return converter(repository.findByIdAndContaBancaria_Id(cartaoId, contaBancariaId)
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado nessa conta!")));
    }

    @Transactional
    public CartaoResDTO atualizar(String id, CartaoReqDTO data) {
        Cartao cartao = buscarEntidadePorId(id);
        if (cartao instanceof CartaoCredito credito) {
            if (data.tipoCartao() != TipoCartaoEnum.CREDITO || data.cartaoCredito() == null || data.cartaoVoucher() != null) {
                throw new BusinessException("O tipo do cartão não pode ser alterado.");
            }
            if (faturaRepository.existsByCartaoDeCredito_Id(id)
                && (!credito.getDiaFechamento().equals(data.cartaoCredito().diaFechamento())
                || !credito.getDiaVencimento().equals(data.cartaoCredito().diaVencimento()))) {
                throw new BusinessException("Não é possível alterar o calendário de um cartão com faturas.");
            }
            credito.setLimiteCredito(data.cartaoCredito().limiteCredito());
            credito.setDiaFechamento(data.cartaoCredito().diaFechamento());
            credito.setDiaVencimento(data.cartaoCredito().diaVencimento());
        } else if (cartao instanceof CartaoVoucher voucher) {
            if (data.tipoCartao() != TipoCartaoEnum.VOUCHER || data.cartaoVoucher() == null || data.cartaoCredito() != null) {
                throw new BusinessException("O tipo do cartão não pode ser alterado.");
            }
            voucher.setDiaRecarga(data.cartaoVoucher().diaRecarga());
            voucher.setValorRecarga(data.cartaoVoucher().valorRecarga());
            voucher.setTipoCartao(data.cartaoVoucher().tipoVoucher());
        } else if (data.tipoCartao() != TipoCartaoEnum.DEBITO || data.cartaoCredito() != null || data.cartaoVoucher() != null) {
            throw new BusinessException("O tipo do cartão não pode ser alterado.");
        }
        cartao.setApelido(data.apelido());
        return converter(cartao);
    }

    @Transactional
    public void excluir(String id) {
        Cartao cartao = buscarEntidadePorId(id);
        var faturas = faturaRepository.findByCartaoDeCredito_Id(id);
        if (registroRepository.existsByCartao_Id(id) || faturas.stream().anyMatch(fatura ->
            fatura.getValorTotal().signum() > 0 || fatura.getStatus() == StatusFaturaEnum.PAGA)) {
            throw new BusinessException("Não é possível excluir um cartão com faturas ou movimentações.");
        }
        faturaRepository.deleteAll(faturas);
        repository.delete(cartao);
    }

    private CartaoResDTO converter(Cartao cartao) {
        return switch (cartao) {
            case CartaoCredito cc -> new CartaoCreditoResDTO(cc);
            case CartaoDebito cd -> new CartaoDebitoResDTO(cd);
            case CartaoVoucher cv -> new CartaoVoucherResDTO(cv);
            default -> throw new IllegalStateException("Tipo de cartão desconhecido.");
        };
    }
}
