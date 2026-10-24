package io.github.raphaelmun1z.gestao_financeira.entities;

import io.github.raphaelmun1z.gestao_financeira.entities.cartao.Cartao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(
    name = "tb_contas_bancarias",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nome_do_banco", "titular_id"})
    }
)
public class ContaBancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "nome_do_banco", nullable = false)
    private String nomeDoBanco;

    private BigDecimal saldoCorrente;
    private BigDecimal saldoInicial;
    private LocalDate dataAbertura;
    private BigDecimal creditoTotal;
    private BigDecimal creditoRestante;

    @ManyToOne
    @JoinColumn(name = "titular_id", nullable = false)
    private Usuario titular;

    @OneToMany(mappedBy = "contaBancaria")
    private final Set<Cartao> cartoes = new HashSet<>();

    public ContaBancaria() {
    }

    public ContaBancaria(
        String nomeDoBanco,
        BigDecimal saldoCorrente,
        BigDecimal creditoTotal,
        BigDecimal creditoRestante,
        Usuario titular
    ) {
        this.nomeDoBanco = nomeDoBanco;
        this.saldoCorrente = saldoCorrente;
        this.saldoInicial = saldoCorrente;
        this.dataAbertura = LocalDate.now();
        this.creditoTotal = creditoTotal;
        this.creditoRestante = creditoRestante;
        this.titular = titular;
    }

    public String getId() {
        return id;
    }

    public String getNomeDoBanco() {
        return nomeDoBanco;
    }

    public void setNomeDoBanco(String nomeDoBanco) {
        this.nomeDoBanco = nomeDoBanco;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public BigDecimal getSaldoCorrente() {
        return saldoCorrente;
    }

    public void setSaldoCorrente(BigDecimal saldoCorrente) {
        this.saldoCorrente = saldoCorrente;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setCreditoRestante(BigDecimal creditoRestante) {
        this.creditoRestante = creditoRestante;
    }

    public BigDecimal getCreditoTotal() {
        return creditoTotal;
    }

    public void setCreditoTotal(BigDecimal creditoTotal) {
        this.creditoTotal = creditoTotal;
    }

    public BigDecimal getCreditoRestante() {
        return creditoRestante;
    }

    public BigDecimal getCreditoUsado() {
        return creditoTotal.subtract(creditoRestante);
    }

    public Usuario getTitular() {
        return titular;
    }

    public Set<Cartao> getCartoes() {
        return cartoes;
    }
}
