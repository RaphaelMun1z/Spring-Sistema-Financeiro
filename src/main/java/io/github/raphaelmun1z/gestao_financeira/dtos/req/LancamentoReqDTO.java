package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.MetodoPagamentoEnum;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoReqDTO(
    @NotNull(message = "O 'valorTotal' é obrigatório!") BigDecimal valorTotal,
    LocalDate dataLancamento,
    String descricao,
    @NotNull(message = "O 'metodoPagamento' é obrigatório!") MetodoPagamentoEnum metodoPagamento,
    Boolean ehRecorrente,
    Integer quantidadeParcelas,
    @NotNull(message = "A 'categoriaId' é obrigatória!") String categoriaId,
    @NotNull(message = "A 'contaBancariaId' é obrigatória!") String contaBancariaId,
    String cartaoId
) {
    public LancamentoReqDTO {
        if (metodoPagamento != MetodoPagamentoEnum.CARTAO_CREDITO
        && metodoPagamento != MetodoPagamentoEnum.CARTAO_DEBITO
        && metodoPagamento != MetodoPagamentoEnum.VOUNCHER) {
            cartaoId = null;
        }

        if (dataLancamento == null) {
            dataLancamento = LocalDate.now();
        }

        if (ehRecorrente == null) {
            ehRecorrente = false;
        }

        if(quantidadeParcelas == null) {
            quantidadeParcelas = 1;
        }
    }
}
