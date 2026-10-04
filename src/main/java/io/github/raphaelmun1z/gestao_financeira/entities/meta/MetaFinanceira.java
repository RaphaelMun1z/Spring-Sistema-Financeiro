package io.github.raphaelmun1z.gestao_financeira.entities.meta;

import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;


@Entity
@Table(name = "tb_metas_financeiras")
public class MetaFinanceira {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String objetivo;
    private BigDecimal valorAlvo;
    private BigDecimal valorAcumulado;
    private YearMonth dataConclusaoPrevista;

    @ManyToOne
    @JoinColumn(name = "criador_id")
    private Usuario criador;

    public MetaFinanceira() {
    }

    public MetaFinanceira(String objetivo, BigDecimal valorAlvo, BigDecimal valorAcumulado, YearMonth dataConclusaoPrevista) {
        this.objetivo = objetivo;
        this.valorAlvo = valorAlvo;
        this.valorAcumulado = valorAcumulado;
        this.dataConclusaoPrevista = dataConclusaoPrevista;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public BigDecimal getValorAlvo() {
        return valorAlvo;
    }

    public void setValorAlvo(BigDecimal valorAlvo) {
        this.valorAlvo = valorAlvo;
    }

    public BigDecimal getValorAcumulado() {
        return valorAcumulado;
    }

    public YearMonth getDataConclusaoPrevista() {
        return dataConclusaoPrevista;
    }

    public void setDataConclusaoPrevista(YearMonth dataConclusaoPrevista) {
        this.dataConclusaoPrevista = dataConclusaoPrevista;
    }

    public Usuario getCriador() {
        return criador;
    }
}
