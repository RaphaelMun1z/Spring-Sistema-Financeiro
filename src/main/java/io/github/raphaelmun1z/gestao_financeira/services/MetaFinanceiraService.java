package io.github.raphaelmun1z.gestao_financeira.services;

import io.github.raphaelmun1z.gestao_financeira.dtos.req.MetaFinanceiraReqDTO;
import io.github.raphaelmun1z.gestao_financeira.dtos.res.MetaFinanceiraResDTO;
import io.github.raphaelmun1z.gestao_financeira.entities.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.repositories.MetaFinanceiraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.NotFoundException;

@Service
public class MetaFinanceiraService {
    private final MetaFinanceiraRepository repository;

    public MetaFinanceiraService(MetaFinanceiraRepository repository) {
        this.repository = repository;
    }

    public MetaFinanceiraResDTO cadastrar(MetaFinanceiraReqDTO data){
        MetaFinanceira novoObj = new MetaFinanceira(
            data.objetivo(),
            data.valorAlvo(),
            data.valorAcumulado(),
            data.dataConclusaoPrevista(),
            UsuarioService.obterUsuarioAutenticado()
        );
        return new MetaFinanceiraResDTO(repository.save(novoObj));
    }

    public List<MetaFinanceiraResDTO> consultarTodos() {
        return repository.findByCriadorId(UsuarioService.obterUsuarioAutenticado().getId()).stream().map(MetaFinanceiraResDTO::new).toList();
    }

    public MetaFinanceiraResDTO consultarPorId(String id) {
        return new MetaFinanceiraResDTO(repository.findByIdAndCriadorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Meta financeira não encontrada!")));
    }

    @Transactional
    public MetaFinanceiraResDTO atualizar(String id, MetaFinanceiraReqDTO data) {
        MetaFinanceira meta = repository.findByIdAndCriadorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Meta financeira não encontrada!"));
        meta.setObjetivo(data.objetivo());
        meta.setValorAlvo(data.valorAlvo());
        meta.setValorAcumulado(data.valorAcumulado());
        meta.setDataConclusaoPrevista(data.dataConclusaoPrevista());
        return new MetaFinanceiraResDTO(meta);
    }

    @Transactional
    public void excluir(String id) {
        MetaFinanceira meta = repository.findByIdAndCriadorId(id, UsuarioService.obterUsuarioAutenticado().getId())
            .orElseThrow(() -> new NotFoundException("Meta financeira não encontrada!"));
        repository.delete(meta);
    }
}
