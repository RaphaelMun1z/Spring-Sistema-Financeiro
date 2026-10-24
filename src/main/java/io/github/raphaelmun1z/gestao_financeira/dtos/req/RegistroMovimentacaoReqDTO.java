package io.github.raphaelmun1z.gestao_financeira.dtos.req;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegistroMovimentacaoReqDTO(
    @NotNull(message = "O valor total é obrigatório!") @Positive @Digits(integer = 17, fraction = 2) BigDecimal valorTotal,
    @NotNull(message = "O método de pagamento é obrigatório!") MetodoPagamentoEnum metodoPagamento,
    @NotBlank(message = "A categoria é obrigatória!") String categoriaId,
    @NotNull(message = "A data do lançamento é obrigatória!") @PastOrPresent LocalDate dataLancamento,
    @Size(max = 255) String descricao,
    @Schema(description = "Quando verdadeiro, o sistema gera uma nova movimentação mensal indefinidamente.") Boolean ehRecorrente,
    @Positive @Max(120) Integer quantidadeParcelas,
    String cartaoId
) {
    public RegistroMovimentacaoReqDTO {
        if (ehRecorrente == null) {
            ehRecorrente = false;
        }
    }
}
