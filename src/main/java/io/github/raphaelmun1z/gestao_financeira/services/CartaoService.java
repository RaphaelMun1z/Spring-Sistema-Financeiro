package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoCreditoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoDebitoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoVoucherResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoDebito;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVoucher;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartaoService {
    private final CartaoRepository repository;
    private final ContaBancariaService contaBancariaService;

    public CartaoService(CartaoRepository cartaoRepository, ContaBancariaService contaBancariaService) {
        this.repository = cartaoRepository;
        this.contaBancariaService = contaBancariaService;
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
            data.cartaoVoucher().saldoCorrente()
        );
        return new CartaoVoucherResDTO(repository.save(cv));
    }

    public Cartao buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado!"));
    }

    public List<CartaoResDTO> consultarTodos() {
        return repository.findAll().stream()
            .<CartaoResDTO>map(cartao -> switch (cartao) {
                case CartaoCredito cc -> new CartaoCreditoResDTO(cc);
                case CartaoDebito cd -> new CartaoDebitoResDTO(cd);
                case CartaoVoucher cv -> new CartaoVoucherResDTO(cv);
                default -> throw new IllegalStateException("Tipo de cartão desconhecido.");
            }).toList();
    }
}
