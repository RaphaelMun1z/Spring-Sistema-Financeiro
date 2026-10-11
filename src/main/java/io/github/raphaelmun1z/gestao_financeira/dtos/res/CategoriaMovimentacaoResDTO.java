package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;

public record CategoriaMovimentacaoResDTO(
    String id,
    String nome,
    TipoCategoriaEnum tipo,
    String cor,
    String icone
) {
    public CategoriaMovimentacaoResDTO(CategoriaDeMovimentacao cm) {
        this(
            cm.getId(),
            cm.getNome(),
            cm.getTipo(),
            cm.getCor(),
            cm.getIcone()
        );
    }
}
