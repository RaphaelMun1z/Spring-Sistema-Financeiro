package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.InvestimentoReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.InvestimentoResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.Investimento;
import io.github.raphaelmun1z.gestao_financeira.repositories.InvestimentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;

@Service
public class InvestimentoService {
    private final InvestimentoRepository repository;

    public InvestimentoService(InvestimentoRepository repository) {
        this.repository = repository;
    }

    public InvestimentoResDTO cadastrar(InvestimentoReqDTO data){
        Investimento novoObj = new Investimento(
            data.categoria(),
            data.dataInicio(),
            data.valorInicial(),
            data.valorCorrente(),
            data.jurosAoMes(),
            UsuarioService.obterUsuarioAutenticado()
        );
        return new InvestimentoResDTO(repository.save(novoObj));
    }

    public List<InvestimentoResDTO> consultarTodos() {
        return repository.findByInvestidorId(UsuarioService.obterUsuarioAutenticado().getId()).stream().map(InvestimentoResDTO::new).toList();
    }

    public InvestimentoResDTO consultarPorId(String id) {
        return new InvestimentoResDTO(repository.findByIdAndInvestidorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Investimento não encontrado!")));
    }

    @Transactional
    public InvestimentoResDTO atualizar(String id, InvestimentoReqDTO data) {
        Investimento investimento = repository.findByIdAndInvestidorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Investimento não encontrado!"));
        if (!investimento.getDataInicio().equals(data.dataInicio())) {
            throw new IllegalArgumentException("A data de início não pode ser alterada após o cadastro.");
        }
        investimento.setCategoria(data.categoria());
        investimento.setValorCorrente(data.valorCorrente());
        investimento.setJurosAoMes(data.jurosAoMes());
        return new InvestimentoResDTO(investimento);
    }

    @Transactional
    public void excluir(String id) {
        Investimento investimento = repository.findByIdAndInvestidorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Investimento não encontrado!"));
        repository.delete(investimento);
    }
}
