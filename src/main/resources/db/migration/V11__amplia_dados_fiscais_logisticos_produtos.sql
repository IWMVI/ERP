ALTER TABLE produtos ADD COLUMN tipo VARCHAR(30) NOT NULL DEFAULT 'PRODUTO';
ALTER TABLE produtos ADD COLUMN ncm VARCHAR(8);
ALTER TABLE produtos ADD COLUMN cest VARCHAR(7);
ALTER TABLE produtos ADD COLUMN origem VARCHAR(60);
ALTER TABLE produtos ADD COLUMN unidade_tributavel VARCHAR(30);
ALTER TABLE produtos ADD COLUMN fator_conversao_tributavel DECIMAL(19, 6) NOT NULL DEFAULT 1;
ALTER TABLE produtos ADD COLUMN tipo_item_sped VARCHAR(40);
ALTER TABLE produtos ADD COLUMN controla_estoque BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE produtos ADD COLUMN peso_liquido DECIMAL(12, 3);
ALTER TABLE produtos ADD COLUMN peso_bruto DECIMAL(12, 3);
ALTER TABLE produtos ADD COLUMN largura DECIMAL(12, 2);
ALTER TABLE produtos ADD COLUMN altura DECIMAL(12, 2);
ALTER TABLE produtos ADD COLUMN comprimento DECIMAL(12, 2);
ALTER TABLE produtos ADD COLUMN volumes INTEGER;
ALTER TABLE produtos ADD COLUMN prazo_preparacao_dias INTEGER;

UPDATE produtos SET unidade_tributavel = unidade_medida WHERE unidade_tributavel IS NULL;
