CREATE TABLE IF NOT EXISTS idosos (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    data_nascimento DATE NOT NULL,
    biografia_breve TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS relatos (
    id SERIAL PRIMARY KEY,
    idoso_id INTEGER NOT NULL REFERENCES idosos(id) ON DELETE CASCADE,
    numero INTEGER NOT NULL,
    texto_bruto TEXT NOT NULL,
    cronica_gerada TEXT NOT NULL,
    data_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_relatos_idoso_numero UNIQUE (idoso_id, numero)
);

CREATE INDEX IF NOT EXISTS idx_idosos_nome ON idosos(nome);
CREATE INDEX IF NOT EXISTS idx_relatos_idoso_id ON relatos(idoso_id);
