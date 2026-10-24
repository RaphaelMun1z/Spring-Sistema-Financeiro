CREATE TABLE tb_usuarios (
    id VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nome_completo VARCHAR(255) NOT NULL,
    papel SMALLINT NOT NULL CHECK (papel BETWEEN 0 AND 1)
);

CREATE TABLE tb_contas_bancarias (
    id VARCHAR(255) PRIMARY KEY,
    nome_do_banco VARCHAR(255) NOT NULL,
    saldo_corrente NUMERIC(38,2) NOT NULL CHECK (saldo_corrente >= 0),
    saldo_inicial NUMERIC(38,2) NOT NULL CHECK (saldo_inicial >= 0),
    data_abertura DATE NOT NULL,
    credito_total NUMERIC(38,2) NOT NULL CHECK (credito_total >= 0),
    credito_restante NUMERIC(38,2) NOT NULL CHECK (credito_restante >= 0 AND credito_restante <= credito_total),
    titular_id VARCHAR(255) NOT NULL REFERENCES tb_usuarios(id),
    UNIQUE (nome_do_banco, titular_id)
);

CREATE TABLE tb_categorias_de_movimentacao (
    id VARCHAR(255) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    tipo SMALLINT NOT NULL CHECK (tipo BETWEEN 0 AND 1),
    cor VARCHAR(255) NOT NULL,
    icone VARCHAR(255) NOT NULL,
    autor_id VARCHAR(255) NOT NULL REFERENCES tb_usuarios(id),
    UNIQUE (nome, autor_id)
);

CREATE TABLE tb_cartoes (
    id VARCHAR(255) PRIMARY KEY,
    apelido VARCHAR(255) NOT NULL,
    conta_bancaria_id VARCHAR(255) NOT NULL REFERENCES tb_contas_bancarias(id),
    UNIQUE (conta_bancaria_id, apelido)
);

CREATE TABLE tb_cartoes_credito (
    id VARCHAR(255) PRIMARY KEY REFERENCES tb_cartoes(id),
    limite_credito NUMERIC(38,2) NOT NULL CHECK (limite_credito > 0),
    dia_fechamento INTEGER NOT NULL CHECK (dia_fechamento BETWEEN 1 AND 31),
    dia_vencimento INTEGER NOT NULL CHECK (dia_vencimento BETWEEN 1 AND 31)
);

CREATE TABLE tb_cartoes_debito (
    id VARCHAR(255) PRIMARY KEY REFERENCES tb_cartoes(id)
);

CREATE TABLE tb_cartoes_vouncher (
    id VARCHAR(255) PRIMARY KEY REFERENCES tb_cartoes(id),
    dia_recarga INTEGER NOT NULL CHECK (dia_recarga BETWEEN 1 AND 31),
    valor_recarga NUMERIC(38,2) NOT NULL CHECK (valor_recarga > 0),
    saldo_corrente NUMERIC(38,2) NOT NULL CHECK (saldo_corrente >= 0),
    ultima_recarga VARCHAR(7),
    tipo_cartao SMALLINT NOT NULL CHECK (tipo_cartao BETWEEN 0 AND 1)
);

CREATE TABLE tb_faturas (
    id VARCHAR(255) PRIMARY KEY,
    valor_total NUMERIC(38,2) NOT NULL CHECK (valor_total >= 0),
    data_referencia VARCHAR(7) NOT NULL,
    status VARCHAR(255) NOT NULL CHECK (status IN ('ABERTA', 'FECHADA', 'PAGA', 'VENCIDA')),
    cartao_credito_id VARCHAR(255) NOT NULL REFERENCES tb_cartoes_credito(id),
    UNIQUE (data_referencia, cartao_credito_id)
);

CREATE TABLE tb_registro_movimentacoes (
    id VARCHAR(255) PRIMARY KEY,
    valor_total NUMERIC(38,2) NOT NULL CHECK (valor_total > 0),
    data_lancamento DATE NOT NULL,
    descricao VARCHAR(255),
    metodo_pagamento SMALLINT NOT NULL CHECK (metodo_pagamento BETWEEN 0 AND 5),
    eh_recorrente BOOLEAN NOT NULL,
    proxima_recorrencia DATE,
    quantidade_parcelas INTEGER NOT NULL CHECK (quantidade_parcelas BETWEEN 1 AND 120),
    categoria_id VARCHAR(255) NOT NULL REFERENCES tb_categorias_de_movimentacao(id),
    conta_bancaria_id VARCHAR(255) NOT NULL REFERENCES tb_contas_bancarias(id),
    cartao_id VARCHAR(255) REFERENCES tb_cartoes(id),
    CHECK ((metodo_pagamento IN (2, 3, 4) AND cartao_id IS NOT NULL)
        OR (metodo_pagamento IN (0, 1, 5) AND cartao_id IS NULL)),
    CHECK (metodo_pagamento = 2 OR quantidade_parcelas = 1)
);

CREATE TABLE tb_lancamentos (
    id VARCHAR(255) PRIMARY KEY,
    valor NUMERIC(38,2) NOT NULL CHECK (valor > 0),
    data_lancamento DATE NOT NULL,
    numero_parcela INTEGER NOT NULL CHECK (numero_parcela > 0),
    registro_de_movimentacao_id VARCHAR(255) NOT NULL REFERENCES tb_registro_movimentacoes(id),
    fatura_id VARCHAR(255) REFERENCES tb_faturas(id),
    UNIQUE (registro_de_movimentacao_id, data_lancamento)
);

CREATE TABLE tb_orcamentos (
    id VARCHAR(255) PRIMARY KEY,
    mes_referencia VARCHAR(255) NOT NULL,
    valor_limite NUMERIC(38,2) NOT NULL CHECK (valor_limite > 0),
    valor_corrente NUMERIC(38,2) NOT NULL CHECK (valor_corrente >= 0),
    categoria_id VARCHAR(255) NOT NULL REFERENCES tb_categorias_de_movimentacao(id),
    UNIQUE (mes_referencia, categoria_id)
);

CREATE TABLE tb_investimentos (
    id VARCHAR(255) PRIMARY KEY,
    categoria VARCHAR(255) NOT NULL,
    data_inicio DATE NOT NULL,
    valor_inicial NUMERIC(38,2) NOT NULL CHECK (valor_inicial >= 0),
    valor_corrente NUMERIC(38,2) NOT NULL CHECK (valor_corrente >= 0),
    juros_ao_mes REAL NOT NULL CHECK (juros_ao_mes >= 0),
    ultima_competencia_juros VARCHAR(7),
    investidor_id VARCHAR(255) NOT NULL REFERENCES tb_usuarios(id),
    UNIQUE (categoria, investidor_id)
);

CREATE TABLE tb_metas_financeiras (
    id VARCHAR(255) PRIMARY KEY,
    objetivo VARCHAR(255) NOT NULL,
    valor_alvo NUMERIC(38,2) NOT NULL CHECK (valor_alvo > 0),
    valor_acumulado NUMERIC(38,2) NOT NULL CHECK (valor_acumulado >= 0),
    data_conclusao_prevista VARCHAR(255) NOT NULL,
    criador_id VARCHAR(255) NOT NULL REFERENCES tb_usuarios(id),
    UNIQUE (objetivo, criador_id)
);

CREATE INDEX idx_contas_titular ON tb_contas_bancarias(titular_id);
CREATE INDEX idx_categorias_autor ON tb_categorias_de_movimentacao(autor_id);
CREATE INDEX idx_investimentos_investidor ON tb_investimentos(investidor_id);
CREATE INDEX idx_metas_criador ON tb_metas_financeiras(criador_id);
CREATE INDEX idx_faturas_cartao_referencia ON tb_faturas(cartao_credito_id, data_referencia);
CREATE INDEX idx_registros_conta_data ON tb_registro_movimentacoes(conta_bancaria_id, data_lancamento);
CREATE INDEX idx_lancamentos_fatura ON tb_lancamentos(fatura_id);
CREATE INDEX idx_orcamentos_categoria ON tb_orcamentos(categoria_id);

CREATE TABLE tb_pagamentos_faturas (
    id VARCHAR(255) PRIMARY KEY,
    fatura_id VARCHAR(255) NOT NULL UNIQUE REFERENCES tb_faturas(id),
    conta_bancaria_id VARCHAR(255) NOT NULL REFERENCES tb_contas_bancarias(id),
    valor NUMERIC(38,2) NOT NULL CHECK (valor > 0),
    data_pagamento DATE NOT NULL
);

CREATE INDEX idx_pagamentos_faturas_conta_data
    ON tb_pagamentos_faturas(conta_bancaria_id, data_pagamento);

CREATE TABLE tb_snapshots_financeiros_mensais (
    id VARCHAR(255) PRIMARY KEY,
    conta_bancaria_id VARCHAR(255) NOT NULL REFERENCES tb_contas_bancarias(id),
    mes_referencia VARCHAR(7) NOT NULL,
    data_fechamento DATE NOT NULL,
    saldo_final NUMERIC(38,2) NOT NULL CHECK (saldo_final >= 0),
    credito_total NUMERIC(38,2) NOT NULL CHECK (credito_total >= 0),
    credito_restante NUMERIC(38,2) NOT NULL CHECK (credito_restante >= 0 AND credito_restante <= credito_total),
    total_entradas NUMERIC(38,2) NOT NULL CHECK (total_entradas >= 0),
    total_saidas NUMERIC(38,2) NOT NULL CHECK (total_saidas >= 0),
    UNIQUE (conta_bancaria_id, mes_referencia)
);

CREATE INDEX idx_snapshots_financeiros_conta_mes
    ON tb_snapshots_financeiros_mensais(conta_bancaria_id, mes_referencia);

CREATE TABLE tb_snapshots_investimentos_mensais (
    id VARCHAR(255) PRIMARY KEY,
    investimento_id VARCHAR(255) NOT NULL REFERENCES tb_investimentos(id),
    mes_referencia VARCHAR(7) NOT NULL,
    data_fechamento DATE NOT NULL,
    valor_final NUMERIC(38,2) NOT NULL CHECK (valor_final >= 0),
    UNIQUE (investimento_id, mes_referencia)
);

CREATE INDEX idx_snapshots_investimentos_investimento_mes
    ON tb_snapshots_investimentos_mensais(investimento_id, mes_referencia);
