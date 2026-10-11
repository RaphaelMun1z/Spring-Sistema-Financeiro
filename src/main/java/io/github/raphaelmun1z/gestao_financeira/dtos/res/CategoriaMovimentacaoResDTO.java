package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.Orcamento;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;

import java.util.Comparator;

public record CategoriaMovimentacaoResDTO(
    String id,
    String nome,
    TipoCategoriaEnum tipo,
    String cor,
    String icone,
    OrcamentoResDTO orcamento
) {
    public CategoriaMovimentacaoResDTO(CategoriaDeMovimentacao cm) {
        this(
            cm.getId(),
            cm.getNome(),
            cm.getTipo(),
            cm.getCor(),
            cm.getIcone(),
            cm.getOrcamentos().stream()
                .max(Comparator.comparing(Orcamento::getMesReferencia))
                .map(OrcamentoResDTO::new)
                .orElse(null)
        );
    }
}
