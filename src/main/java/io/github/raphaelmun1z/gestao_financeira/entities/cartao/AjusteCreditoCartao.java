package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_ajustes_credito_cartao")
public class AjusteCreditoCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartao_credito_id", nullable = false)
    private CartaoCredito cartaoCredito;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "credito_utilizado_desejado", nullable = false, precision = 38, scale = 2)
    private BigDecimal creditoUtilizadoDesejado;

    @Column(name = "credito_utilizado_anterior", nullable = false, precision = 38, scale = 2)
    private BigDecimal creditoUtilizadoAnterior;

    @Column(name = "credito_faturas_abertas_no_ajuste", nullable = false, precision = 38, scale = 2)
    private BigDecimal creditoFaturasAbertasNoAjuste;

    @Column(name = "registrado_em", nullable = false)
    private LocalDateTime registradoEm;

    protected AjusteCreditoCartao() {}

    public AjusteCreditoCartao(CartaoCredito cartaoCredito, Usuario usuario, BigDecimal desejado, BigDecimal anterior,
                               BigDecimal faturasAbertasNoAjuste) {
        this.cartaoCredito = cartaoCredito;
        this.usuario = usuario;
        this.creditoUtilizadoDesejado = desejado;
        this.creditoUtilizadoAnterior = anterior;
        this.creditoFaturasAbertasNoAjuste = faturasAbertasNoAjuste;
        this.registradoEm = LocalDateTime.now();
    }

    public String getId() { return id; }
    public CartaoCredito getCartaoCredito() { return cartaoCredito; }
    public Usuario getUsuario() { return usuario; }
    public BigDecimal getCreditoUtilizadoDesejado() { return creditoUtilizadoDesejado; }
    public BigDecimal getCreditoUtilizadoAnterior() { return creditoUtilizadoAnterior; }
    public BigDecimal getCreditoFaturasAbertasNoAjuste() { return creditoFaturasAbertasNoAjuste; }
    public LocalDateTime getRegistradoEm() { return registradoEm; }
}
