package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

public record LancamentoReqDTO(
    @NotNull(message = "O 'valorTotal' é obrigatório!") BigDecimal valorTotal,
    Date dataLancamento,
    String descricao,
    @NotNull(message = "O 'metodoPagamento' é obrigatório!") MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    @NotNull(message = "A 'categoriaId' é obrigatória!") String categoriaId,
    @NotNull(message = "A 'contaBancariaId' é obrigatória!") String contaBancariaId,
    String cartaoId
) {
    public LancamentoReqDTO {
        if (metodoPagamento != MetodoPagamentoEnum.CARTAO) {
            cartaoId = null;
        }

        if (dataLancamento == null) {
            dataLancamento = new Date();
        }

        if (ehRecorrente == null) {
            ehRecorrente = false;
        }
    }
}
