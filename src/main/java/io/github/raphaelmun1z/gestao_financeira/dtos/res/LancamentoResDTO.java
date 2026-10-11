package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.Lancamento;
import io.github.raphaelmun1z.gestao_financeira.entities.RegistroDeMovimentacao;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.MetodoPagamentoEnum;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoResDTO(
    BigDecimal valor,
    LocalDate dataLancamento,
    String descricao
) {
    public LancamentoResDTO(Lancamento l) {
        this(l.getValor(), l.getDataLancamento(), l.getRegistroDeMovimentacao().getDescricao());
    }
}
