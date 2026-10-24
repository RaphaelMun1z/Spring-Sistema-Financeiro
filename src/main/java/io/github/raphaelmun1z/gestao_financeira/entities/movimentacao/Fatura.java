package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.CartaoCredito;
import io.github.raphaelmun1z.gestao_financeira.entities.enums.StatusFaturaEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.LocalDate;
import io.github.raphaelmun1z.gestao_financeira.exceptions.models.BusinessException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "tb_faturas",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"data_referencia", "cartao_credito_id"})
    }
)
public class Fatura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;

    @Column(name = "data_referencia", nullable = false, length = 7)
    private YearMonth dataReferencia;

    @Enumerated(EnumType.STRING)
    private StatusFaturaEnum status;

    @ManyToOne
    @JoinColumn(name = "cartao_credito_id", nullable = false)
    private CartaoCredito cartaoDeCredito;

    @OneToMany(mappedBy = "fatura")
    private final Set<Lancamento> lancamentos = new HashSet<>();

    @OneToOne(mappedBy = "fatura")
    private PagamentoFatura pagamento;

    public Fatura() {
    }

    public Fatura(
        YearMonth dataReferencia,
        CartaoCredito cartaoDeCredito
    ) {
        this.valorTotal = BigDecimal.valueOf(0);
        this.dataReferencia = dataReferencia;
        this.status = StatusFaturaEnum.ABERTA;
        this.cartaoDeCredito = cartaoDeCredito;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public YearMonth getDataReferencia() {
        return dataReferencia;
    }

    public StatusFaturaEnum getStatus() {
        return status;
    }

    public CartaoCredito getCartaoDeCredito() {
        return cartaoDeCredito;
    }

    public Set<Lancamento> getLancamentos() {
        return lancamentos;
    }

    public PagamentoFatura getPagamento() {
        return pagamento;
    }

    public void registrarPagamento(PagamentoFatura pagamento) {
        this.pagamento = pagamento;
    }

    public LocalDate getDataVencimento() {
        return dataReferencia.atDay(Math.min(cartaoDeCredito.getDiaVencimento(), dataReferencia.lengthOfMonth()));
    }

    public LocalDate getDataFechamento() {
        YearMonth mes = cartaoDeCredito.getDiaVencimento() <= cartaoDeCredito.getDiaFechamento()
            ? dataReferencia.minusMonths(1) : dataReferencia;
        return mes.atDay(Math.min(cartaoDeCredito.getDiaFechamento(), mes.lengthOfMonth()));
    }

    public void recalcular() {
        if (status == StatusFaturaEnum.PAGA) {
            throw new BusinessException("Não é possível alterar lançamentos de uma fatura paga.");
        }
        valorTotal = lancamentos.stream().map(Lancamento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        atualizarStatus();
    }

    public void atualizarStatus() {
        if (status == StatusFaturaEnum.PAGA) {
            return;
        }
        status = calcularStatus(LocalDate.now());
    }

    public StatusFaturaEnum calcularStatus(LocalDate hoje) {
        if (status == StatusFaturaEnum.PAGA) {
            return StatusFaturaEnum.PAGA;
        }
        if (hoje.isAfter(getDataVencimento()) && valorTotal.signum() > 0) {
            return StatusFaturaEnum.VENCIDA;
        } else if (!hoje.isBefore(getDataFechamento())) {
            return StatusFaturaEnum.FECHADA;
        } else {
            return StatusFaturaEnum.ABERTA;
        }
    }

    public void pagar() {
        if (valorTotal.signum() <= 0) {
            throw new BusinessException("A fatura não possui valor a pagar.");
        }
        status = StatusFaturaEnum.PAGA;
    }
}
