package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.CategoriaDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;

public record CategoriaDeMovimentacaoResDTO(
    String id,
    String nome,
    TipoCategoriaEnum tipo,
    String cor,
    String icone
) {
    public CategoriaDeMovimentacaoResDTO(CategoriaDeMovimentacao categoria) {
        this(
            categoria.getId(),
            categoria.getNome(),
            categoria.getTipo(),
            categoria.getCor(),
            categoria.getIcone()
        );
    }
}