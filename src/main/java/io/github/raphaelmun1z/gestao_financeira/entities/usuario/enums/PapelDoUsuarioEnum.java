package io.github.raphaelmun1z.gestao_financeira.entities.usuario.enums;

public enum PapelDoUsuarioEnum {
    PADRAO("padrao"),
    ADMINISTRADOR("administrador");

    private String papel;

    PapelDoUsuarioEnum(String papel) {
        this.papel = papel;
    }

    public String getPapel() {
        return papel;
    }
}
