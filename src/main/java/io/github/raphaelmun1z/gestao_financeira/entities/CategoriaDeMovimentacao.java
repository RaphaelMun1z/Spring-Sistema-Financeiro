package io.github.raphaelmun1z.gestao_financeira.entities;

import io.github.raphaelmun1z.gestao_financeira.entities.enums.TipoCategoriaEnum;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "tb_categorias_de_movimentacao")
public class CategoriaDeMovimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nome;
    private TipoCategoriaEnum tipo;
    private String cor;
    private String icone;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @OneToMany(mappedBy = "categoria")
    private Set<Orcamento> orcamentos;

    public CategoriaDeMovimentacao() {
    }

    public CategoriaDeMovimentacao(
        String nome,
        TipoCategoriaEnum tipo,
        String cor,
        String icone,
        Usuario autor
    ) {
        this.nome = nome;
        this.tipo = tipo;
        this.cor = cor;
        this.icone = icone;
        this.autor = autor;
    }

    public String getId() {
        return id;
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
