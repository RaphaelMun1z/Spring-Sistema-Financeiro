package io.github.raphaelmun1z.gestao_financeira.entities.cartao;

import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;

public abstract class Cartao {
    private String apelido;
    private ContaBancaria contaBancaria;

    public Cartao() {
    }

    public Cartao(String apelido, ContaBancaria contaBancaria) {
        this.apelido = apelido;
        this.contaBancaria = contaBancaria;
    }
}