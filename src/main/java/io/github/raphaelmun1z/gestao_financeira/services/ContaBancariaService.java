package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.req.ContaBancariaAtualizacaoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.ContaBancariaResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;
import io.github.raphaelmun1z.gestao_financeira.repositories.ContaBancariaRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.CartaoRepository;
import io.github.raphaelmun1z.gestao_financeira.repositories.RegistroDeMovimentacaoRepository;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaBancariaService {
    private final ContaBancariaRepository repository;
    private final CartaoRepository cartaoRepository;
    private final RegistroDeMovimentacaoRepository registroRepository;

    public ContaBancariaService(ContaBancariaRepository repository, CartaoRepository cartaoRepository, RegistroDeMovimentacaoRepository registroRepository) {
        this.repository = repository;
        this.cartaoRepository = cartaoRepository;
        this.registroRepository = registroRepository;
    }

    @Transactional
    public ContaBancariaResDTO cadastrar(ContaBancariaReqDTO data) {
        ContaBancaria novoObj = new ContaBancaria(
            data.nomeDoBanco(),
            data.saldoCorrente(),
            data.creditoTotal(),
            data.creditoRestante(),
            UsuarioService.obterUsuarioAutenticado()
        );
        return new ContaBancariaResDTO(repository.save(novoObj));
    }

    public ContaBancaria buscarEntidadePorId(String id) {
        return repository.findByIdAndTitularId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Conta bancária não encontrada!"));
    }

    public List<ContaBancariaResDTO> consultarTodos() {
        return repository.findByTitularId(UsuarioService.obterUsuarioAutenticado().getId()).stream().map(ContaBancariaResDTO::new).toList();
    }

    public ContaBancariaResDTO consultarPorId(String id) {
        return new ContaBancariaResDTO(buscarEntidadePorId(id));
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
