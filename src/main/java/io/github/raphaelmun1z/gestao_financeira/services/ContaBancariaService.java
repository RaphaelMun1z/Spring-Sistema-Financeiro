package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaAtualizacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.FaturaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.AjusteCreditoCartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import java.math.BigDecimal;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaBancariaService {
    private final ContaBancariaRepository repository;
    private final CartaoRepository cartaoRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;
    private final FaturaRepository faturaRepository;
    private final AjusteCreditoCartaoRepository ajusteRepository;

    public ContaBancariaService(ContaBancariaRepository repository, CartaoRepository cartaoRepository, RegistroDeMovimentacaoRepository registroRepository, FaturaRepository faturaRepository, AjusteCreditoCartaoRepository ajusteRepository) {
        this.repository = repository;
        this.cartaoRepository = cartaoRepository;
        this.registroRepository = registroRepository;
        this.faturaRepository = faturaRepository;
        this.ajusteRepository = ajusteRepository;
    }

    @Transactional
    public ContaBancariaResDTO cadastrar(ContaBancariaReqDTO data) {
        ContaBancaria novoObj = new ContaBancaria(
            data.nomeDoBanco(),
            data.saldoCorrente(),
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            UsuarioService.obterUsuarioAutenticado()
        );
        return new ContaBancariaResDTO(repository.save(novoObj));
    }

    public ContaBancaria buscarEntidadePorId(String id) {
        return repository.findByIdAndTitularId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Conta bancária não encontrada!"));
    }

    public List<ContaBancariaResDTO> consultarTodos() {
        return repository.findByTitularId(UsuarioService.obterUsuarioAutenticado().getId()).stream().map(this::paraResposta).toList();
    }

    public ContaBancariaResDTO consultarPorId(String id) {
        return paraResposta(buscarEntidadePorId(id));
    }

    private ContaBancariaResDTO paraResposta(ContaBancaria conta) {
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal usado = BigDecimal.ZERO;
        for (CartaoCredito cartao : cartaoRepository.findCartoesDeCreditoDaConta(conta.getId())) {
            total = total.add(cartao.getLimiteCredito());
            BigDecimal faturas = faturaRepository.somarTotalPorCartaoEStatusDiferente(cartao.getId(), StatusFaturaEnum.PAGA);
            BigDecimal ajuste = ajusteRepository.findFirstByCartaoCredito_IdOrderByRegistradoEmDescIdDesc(cartao.getId())
                .map(item -> item.getCreditoUtilizadoDesejado().subtract(item.getCreditoFaturasAbertasNoAjuste()))
                .orElse(BigDecimal.ZERO);
            usado = usado.add(faturas.add(ajuste).max(BigDecimal.ZERO));
        }
        return new ContaBancariaResDTO(conta, total, usado, total.subtract(usado));
    }

    @Transactional
    public ContaBancariaResDTO atualizar(String id, ContaBancariaAtualizacaoReqDTO data) {
        ContaBancaria conta = buscarEntidadeParaAtualizar(id);
        conta.setNomeDoBanco(data.nomeDoBanco());
        return new ContaBancariaResDTO(conta);
    }

    @Transactional
    public void excluir(String id) {
        ContaBancaria conta = buscarEntidadeParaAtualizar(id);
        if (cartaoRepository.countByContaBancaria_Id(id) > 0 || registroRepository.existsByContaBancaria_Id(id)) {
            throw new BusinessException("Não é possível excluir uma conta com cartões ou movimentações.");
        }
        repository.delete(conta);
    }

    public ContaBancaria buscarEntidadeParaAtualizar(String id) {
        return repository.buscarPorIdParaAtualizar(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Conta bancária não encontrada!"));
    }
}
