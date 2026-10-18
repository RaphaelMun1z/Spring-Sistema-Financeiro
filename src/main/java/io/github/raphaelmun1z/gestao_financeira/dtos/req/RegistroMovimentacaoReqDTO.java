package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroMovimentacaoReqDTO(
    @NotNull(message = "O valor total é obrigatório!") BigDecimal valorTotal,
    @NotNull(message = "O método de pagamento é obrigatório!") MetodoPagamentoEnum metodoPagamento,
    @NotBlank(message = "A categoria é obrigatória!") String categoriaId,
    LocalDate dataLancamento,
    String descricao,
    Boolean ehRecorrente,
    Integer quantidadeParcelas,
    String cartaoId
) {
}
