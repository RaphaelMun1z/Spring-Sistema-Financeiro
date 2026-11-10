CREATE TABLE tb_ajustes_credito_cartao (
    id VARCHAR(255) PRIMARY KEY,
    cartao_credito_id VARCHAR(255) NOT NULL REFERENCES tb_cartoes_credito(id),
    usuario_id VARCHAR(255) NOT NULL REFERENCES tb_usuarios(id),
    registrado_em TIMESTAMP NOT NULL,
    credito_utilizado_anterior NUMERIC(38,2) NOT NULL,
    credito_faturas_abertas_no_ajuste NUMERIC(38,2) NOT NULL,
    credito_utilizado_desejado NUMERIC(38,2) NOT NULL CHECK (credito_utilizado_desejado >= 0),
    CHECK (credito_utilizado_anterior >= 0),
    CHECK (credito_faturas_abertas_no_ajuste >= 0)
);

CREATE INDEX idx_ajustes_credito_cartao_data
    ON tb_ajustes_credito_cartao(cartao_credito_id, registrado_em DESC, id DESC);
