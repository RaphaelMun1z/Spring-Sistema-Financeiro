package io.github.raphaelmun1z.gestao_financeira.dtos.res;

import io.github.raphaelmun1z.gestao_financeira.entities.ContaBancaria;

import java.math.BigDecimal;

public record ContaBancariaResDTO(
    String id,
    String nomeDoBanco,
    BigDecimal saldoCorrente,
    BigDecimal saldoInicial,
    java.time.LocalDate dataAbertura,
    BigDecimal creditoTotal,
    BigDecimal creditoUsado,
    BigDecimal creditoRestante
) {
    public ContaBancariaResDTO(ContaBancaria contaBancaria) {
        this(
            contaBancaria.getId(),
            contaBancaria.getNomeDoBanco(),
            contaBancaria.getSaldoCorrente(),
            contaBancaria.getSaldoInicial(),
            contaBancaria.getDataAbertura(),
            contaBancaria.getCreditoTotal(),
            contaBancaria.getCreditoUsado(),
            contaBancaria.getCreditoRestante()
        );
    }
}
