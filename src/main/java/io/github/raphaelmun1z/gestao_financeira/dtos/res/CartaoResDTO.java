package io.github.raphaelmun1z.gestao_financeira.dtos.res;

public sealed interface CartaoResDTO permits CartaoPadraoResDTO, CartaoVouncherResDTO {
    String apelido();
    String contaBancariaId();
}