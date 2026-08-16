CREATE TABLE pedidos_venda (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES clientes(id),
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    desconto NUMERIC(19,2) NOT NULL DEFAULT 0
);

CREATE TABLE itens_pedido_venda (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos_venda(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade NUMERIC(19,3) NOT NULL CHECK (quantidade > 0),
    preco_unitario NUMERIC(19,2) NOT NULL CHECK (preco_unitario >= 0)
);

CREATE TABLE pedidos_compra (
    id BIGSERIAL PRIMARY KEY,
    fornecedor_id BIGINT NOT NULL REFERENCES fornecedores(id),
    data_criacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE itens_pedido_compra (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL REFERENCES pedidos_compra(id) ON DELETE CASCADE,
    produto_id BIGINT NOT NULL REFERENCES produtos(id),
    quantidade NUMERIC(19,3) NOT NULL CHECK (quantidade > 0),
    custo_unitario NUMERIC(19,2) NOT NULL CHECK (custo_unitario >= 0)
);

CREATE TABLE titulos_financeiros (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,
    status VARCHAR(12) NOT NULL,
    cliente_id BIGINT REFERENCES clientes(id),
    fornecedor_id BIGINT REFERENCES fornecedores(id),
    valor NUMERIC(19,2) NOT NULL CHECK (valor > 0),
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    parcela INTEGER NOT NULL CHECK (parcela > 0),
    total_parcelas INTEGER NOT NULL CHECK (total_parcelas > 0),
    origem_tipo VARCHAR(30) NOT NULL,
    origem_id BIGINT NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    CONSTRAINT uk_titulo_origem_parcela UNIQUE (tipo, origem_tipo, origem_id, parcela),
    CONSTRAINT ck_titulo_parte CHECK (
        (tipo = 'RECEBER' AND cliente_id IS NOT NULL AND fornecedor_id IS NULL)
        OR (tipo = 'PAGAR' AND fornecedor_id IS NOT NULL AND cliente_id IS NULL)
    )
);

CREATE INDEX idx_pedidos_venda_cliente ON pedidos_venda(cliente_id);
CREATE INDEX idx_itens_pedido_venda_pedido ON itens_pedido_venda(pedido_id);
CREATE INDEX idx_pedidos_compra_fornecedor ON pedidos_compra(fornecedor_id);
CREATE INDEX idx_itens_pedido_compra_pedido ON itens_pedido_compra(pedido_id);
CREATE INDEX idx_titulos_status_vencimento ON titulos_financeiros(status, data_vencimento);
