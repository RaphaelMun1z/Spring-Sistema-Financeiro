package io.github.raphaelmun1z.gestao_financeira.entities.usuario;

import io.github.raphaelmun1z.gestao_financeira.entities.conta.ContaBancaria;
import io.github.raphaelmun1z.gestao_financeira.entities.meta.MetaFinanceira;
import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.CategoriaDeMovimentacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Usuario {
    private String nomeCompleto;
    private String email;
    private String senha;
    private Set<CategoriaDeMovimentacao> categoriasDeMovimentacao = new HashSet<>();
    private Set<ContaBancaria> contasBancarias = new HashSet<>();
    private List<MetaFinanceira> metasFinanceiras = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(String nomeCompleto, String email, String senha) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public Set<CategoriaDeMovimentacao> getCategoriasDeMovimentacao() {
        return categoriasDeMovimentacao;
    }

    public Set<ContaBancaria> getContasBancarias() {
        return contasBancarias;
    }

    public List<MetaFinanceira> getMetasFinanceiras() {
        return metasFinanceiras;
    }
}
