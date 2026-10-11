package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_cartoes_debito")
public class CartaoDebito extends Cartao {
    public CartaoDebito() {
    }

    public CartaoDebito(String apelido, ContaBancaria contaBancaria) {
        super(apelido, contaBancaria);
    }
}
