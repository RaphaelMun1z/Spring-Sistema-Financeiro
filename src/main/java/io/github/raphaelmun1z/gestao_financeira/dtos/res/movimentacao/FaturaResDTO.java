package io.github.raphaelmun1z.gestao_financeira.dtos.res.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Fatura;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.Lancamento;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public record FaturaResDTO(
    String id,
    BigDecimal valorTotal,
    YearMonth dataReferencia,
    StatusFaturaEnum status,
    String cartaoDeCreditoId,
    List<LancamentoResDTO> lancamentos
) {
    public FaturaResDTO(Fatura f) {
        this(
            f.getId(),
            f.getValorTotal(),
            f.getDataReferencia(),
            f.getStatus(),
            f.getCartaoDeCredito().getId(),
            f.getLancamentos().stream()
                .map(LancamentoResDTO::new)
                .toList()
        );
    }
}
