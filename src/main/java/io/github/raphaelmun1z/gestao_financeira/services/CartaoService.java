package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.CartaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoPadraoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoResDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.CartaoVouncherResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoPadrao;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoVouncher;
import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartaoService {
    private final CartaoRepository repository;
    private final ContaBancariaService contaBancariaService;

    public CartaoService(CartaoRepository cartaoRepository, ContaBancariaService contaBancariaService) {
        this.repository = cartaoRepository;
        this.contaBancariaService = contaBancariaService;
    }

    public CartaoResDTO cadastrar(CartaoReqDTO data) {
        if (data.cartaoPadrao() == null && data.cartaoVouncher() == null) {
            throw new BusinessException("É necessário informar um cartão padrão ou vouncher!");
        }

        ContaBancaria contaBancaria = contaBancariaService.buscarEntidadePorId(data.contaBancariaId());

        if (data.cartaoPadrao() != null) {
            CartaoPadrao cartaoPadrao = new CartaoPadrao(
                data.apelido(),
                contaBancaria,
                data.cartaoPadrao().limiteCredito(),
                data.cartaoPadrao().diaFechamento(),
                data.cartaoPadrao().diaVencimento(),
                data.cartaoPadrao().tipoCartao()
            );

            return new CartaoPadraoResDTO(repository.save(cartaoPadrao));
        }

        CartaoVouncher cartaoVouncher = new CartaoVouncher(
            data.apelido(),
            contaBancaria,
            data.cartaoVouncher().diaRecarga(),
            data.cartaoVouncher().valorRecarga(),
            data.cartaoVouncher().saldoCorrente(),
            data.cartaoVouncher().tipoCartao()
        );

        return new CartaoVouncherResDTO(repository.save(cartaoVouncher));
    }

    public Cartao buscarEntidadePorId(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Cartão não encontrado!"));
    }

    public List<CartaoResDTO> consultarTodos() {
        return repository.findAll().stream()
            .<CartaoResDTO>map(cartao -> {
                if (cartao instanceof CartaoPadrao p) {
                    return new CartaoPadraoResDTO(p);
                }

                if (cartao instanceof CartaoVouncher v) {
                    return new CartaoVouncherResDTO(v);
                }

                throw new IllegalStateException("Tipo de cartão desconhecido.");
            }).toList();
    }
}
