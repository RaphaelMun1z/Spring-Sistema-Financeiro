package io.github.raphaelmun1z.gestao_financeira.entities.usuario.enums;

public enum PapelDoUsuarioEnum {
    PAPEL_PADRAO("papel_padrao"),
    PAPEL_ADMINISTRADOR("papel_administrador");

    private String papel;

    PapelDoUsuarioEnum(String papel) {
        this.papel = papel;
    }

    public String getPapel() {
        return papel;
    }
}
