-- status do cliente, usado pela lambda de autenticação por CPF
ALTER TABLE clientes ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE clientes ALTER COLUMN tipo_documento SET NOT NULL;
ALTER TABLE clientes ADD CONSTRAINT ck_clientes_tipo_documento CHECK (tipo_documento IN ('CPF', 'CNPJ'));

-- unique já cria índice, esses eram duplicados
DROP INDEX idx_documento;
DROP INDEX idx_placa;
DROP INDEX idx_produto_sku;
DROP INDEX idx_produto_ean;
DROP INDEX idx_os_tag_chave;

-- fks que não tinham índice
CREATE INDEX idx_modelo_marca_id ON modelo (marca_id);
CREATE INDEX idx_servico_tipo_pecas_servico_id ON servico_tipo_pecas (servico_id);
CREATE INDEX idx_servico_os_os_id ON servico_os (ordem_de_servico_id);
CREATE INDEX idx_servico_os_servico_id ON servico_os (servico_id);
CREATE INDEX idx_peca_os_servico_os_id ON peca_os (servico_os_id);
CREATE INDEX idx_peca_os_produto_id ON peca_os (produto_id);
CREATE INDEX idx_insumo_os_servico_os_id ON insumo_os (servico_os_id);
CREATE INDEX idx_insumo_os_produto_id ON insumo_os (produto_id);

-- histórico da os em ordem (tempo médio por status) e filtros/volume por data
CREATE INDEX idx_status_changes_os_created_at ON status_changes (ordem_de_servico_id, created_at);
CREATE INDEX idx_os_created_at ON ordens_de_servico (created_at);

-- o domínio já barra esses casos, aqui é só garantia no banco
ALTER TABLE produtos ADD CONSTRAINT ck_produtos_estoque CHECK (estoque >= 0);
ALTER TABLE produtos ADD CONSTRAINT ck_produtos_estoque_reservado CHECK (estoque_reservado >= 0);
ALTER TABLE produtos ADD CONSTRAINT ck_produtos_preco CHECK (preco >= 0);

ALTER TABLE aplicacao_produtos ADD CONSTRAINT ck_aplicacao_quantidade CHECK (quantidade > 0);
ALTER TABLE aplicacao_produtos ADD CONSTRAINT ck_aplicacao_anos CHECK (ano_fim >= ano_inicio);

ALTER TABLE servicos ADD CONSTRAINT ck_servicos_horas_tecnicas CHECK (horas_tecnicas >= 0);
ALTER TABLE servico_tipo_pecas ADD CONSTRAINT ck_servico_tipo_pecas_quantidade CHECK (quantidade > 0);

ALTER TABLE peca_os ADD CONSTRAINT ck_peca_os_quantidade CHECK (quantidade > 0);
ALTER TABLE peca_os ADD CONSTRAINT ck_peca_os_preco CHECK (preco_total >= 0);
ALTER TABLE insumo_os ADD CONSTRAINT ck_insumo_os_quantidade CHECK (quantidade > 0);
ALTER TABLE insumo_os ADD CONSTRAINT ck_insumo_os_preco CHECK (preco_total >= 0);

ALTER TABLE ordens_de_servico ADD CONSTRAINT ck_os_precos CHECK (
    preco_total >= 0
    AND preco_servicos_desejados >= 0
    AND preco_servicos_necessarios >= 0
    AND preco_servicos_adicionais >= 0
);
