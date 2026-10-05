package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.pagamento.Parcela;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Set;

@Entity
@Table(name = "tb_registros_despesas")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class RegistroDeDespesas {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal valorTotal;
    private YearMonth dataReferencia;

    @ManyToOne
    @JoinColumn(name = "cartao_id")
    private Cartao cartao;

    @OneToMany(mappedBy = "registroDeDespesas")
    private Set<Parcela> despesas;

    public RegistroDeDespesas() {
    }

    public RegistroDeDespesas(BigDecimal valorTotal, YearMonth dataReferencia) {
        this.valorTotal = valorTotal;
        this.dataReferencia = dataReferencia;
    }
}
