package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_cartoes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String apelido;

    @ManyToOne
    @JoinColumn(name = "conta_bancaria_id")
    private ContaBancaria contaBancaria;

    public Cartao() {
    }

    public Cartao(String apelido, ContaBancaria contaBancaria) {
        this.apelido = apelido;
        this.contaBancaria = contaBancaria;
    }

    public String getId() {
        return id;
    }

    public String getApelido() {
        return apelido;
    }
}