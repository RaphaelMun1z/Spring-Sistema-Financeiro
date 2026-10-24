package io.github.raphaelmun1z.gestao_financeira.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(
    name = "tb_investimentos",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"categoria", "investidor_id"})
    }
)
public class Investimento {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String categoria;

    private LocalDate dataInicio;
    private BigDecimal valorInicial;
    private BigDecimal valorCorrente;
    private Float jurosAoMes;
    @Column(name = "ultima_competencia_juros", length = 7)
    private YearMonth ultimaCompetenciaJuros;

    @ManyToOne
    @JoinColumn(name = "investidor_id", nullable = false)
    private Usuario investidor;

    public Investimento() {
    }

    public Investimento(
        String categoria,
        LocalDate dataInicio,
        BigDecimal valorInicial,
        BigDecimal valorCorrente,
        Float jurosAoMes,
        Usuario investidor
    ) {
        this.categoria = categoria;
        this.dataInicio = dataInicio;
        this.valorInicial = valorInicial;
        this.valorCorrente = valorCorrente;
        this.jurosAoMes = jurosAoMes;
        this.investidor = investidor;
        this.ultimaCompetenciaJuros = YearMonth.from(dataInicio);
    }

    public String getId() {
        return id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public BigDecimal getValorCorrente() {
        return valorCorrente;
    }

    public void setValorCorrente(BigDecimal valorCorrente) {
        this.valorCorrente = valorCorrente;
    }

    public Float getJurosAoMes() {
        return jurosAoMes;
    }

    public void setJurosAoMes(Float jurosAoMes) {
        this.jurosAoMes = jurosAoMes;
    }

    public Usuario getInvestidor() {
        return investidor;
    }

    public YearMonth getUltimaCompetenciaJuros() {
        return ultimaCompetenciaJuros;
    }

    public void aplicarRendimentosAte(LocalDate hoje) {
        YearMonth competencia = ultimaCompetenciaJuros.plusMonths(1);
        BigDecimal taxa = BigDecimal.valueOf(jurosAoMes.doubleValue()).movePointLeft(2);
        while (!competencia.atDay(Math.min(dataInicio.getDayOfMonth(), competencia.lengthOfMonth())).isAfter(hoje)) {
            valorCorrente = valorCorrente.multiply(BigDecimal.ONE.add(taxa)).setScale(2, java.math.RoundingMode.HALF_UP);
            ultimaCompetenciaJuros = competencia;
            competencia = competencia.plusMonths(1);
        }
    }
}
