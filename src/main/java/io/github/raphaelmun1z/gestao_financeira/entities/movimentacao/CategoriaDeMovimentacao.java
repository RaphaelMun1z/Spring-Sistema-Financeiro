package io.github.raphaelmun1z.gestao_financeira.entities.movimentacao;

import io.github.raphaelmun1z.gestao_financeira.entities.movimentacao.enums.TipoCategoriaEnum;
import io.github.raphaelmun1z.gestao_financeira.entities.orcamento.Orcamento;
import io.github.raphaelmun1z.gestao_financeira.entities.usuario.Usuario;

import java.util.Set;

public class CategoriaDeMovimentacao {
    private String nome;
    private TipoCategoriaEnum tipo;
    private String cor;
    private String icone;
    private Usuario autor;
    private Set<Orcamento> orcamentos;

    public CategoriaDeMovimentacao() {
    }

    public CategoriaDeMovimentacao(String nome, TipoCategoriaEnum tipo, String cor, String icone) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
        this.icone = icone;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoCategoriaEnum getTipo() {
        return tipo;
    }

    public void setTipo(TipoCategoriaEnum tipo) {
        this.tipo = tipo;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public Usuario getAutor() {
        return autor;
    }

    public Set<Orcamento> getOrcamentos() {
        return orcamentos;
    }
}
