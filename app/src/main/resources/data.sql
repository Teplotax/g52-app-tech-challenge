INSERT INTO marca (nome) VALUES ('Toyota');
INSERT INTO marca (nome) VALUES ('Honda');
INSERT INTO marca (nome) VALUES ('Volkswagen');
INSERT INTO marca (nome) VALUES ('Chevrolet');
INSERT INTO marca (nome) VALUES ('Ford');
INSERT INTO marca (nome) VALUES ('Fiat');
INSERT INTO marca (nome) VALUES ('Hyundai');
INSERT INTO marca (nome) VALUES ('Renault');
INSERT INTO marca (nome) VALUES ('Nissan');
INSERT INTO marca (nome) VALUES ('BMW');


-- Toyota (marca_id = 1)
INSERT INTO modelo (nome, marca_id) VALUES ('Corolla', 1);
INSERT INTO modelo (nome, marca_id) VALUES ('Hilux', 1);
INSERT INTO modelo (nome, marca_id) VALUES ('Yaris', 1);

-- Honda (marca_id = 2)
INSERT INTO modelo (nome, marca_id) VALUES ('Civic', 2);
INSERT INTO modelo (nome, marca_id) VALUES ('HR-V', 2);
INSERT INTO modelo (nome, marca_id) VALUES ('Fit', 2);

-- Volkswagen (marca_id = 3)
INSERT INTO modelo (nome, marca_id) VALUES ('Gol', 3);
INSERT INTO modelo (nome, marca_id) VALUES ('Polo', 3);
INSERT INTO modelo (nome, marca_id) VALUES ('Tiguan', 3);
INSERT INTO modelo (nome, marca_id) VALUES ('Spacefox', 3);

-- Chevrolet (marca_id = 4)
INSERT INTO modelo (nome, marca_id) VALUES ('Onix', 4);
INSERT INTO modelo (nome, marca_id) VALUES ('Tracker', 4);
INSERT INTO modelo (nome, marca_id) VALUES ('S10', 4);

-- Ford (marca_id = 5)
INSERT INTO modelo (nome, marca_id) VALUES ('Ka', 5);
INSERT INTO modelo (nome, marca_id) VALUES ('Ranger', 5);
INSERT INTO modelo (nome, marca_id) VALUES ('EcoSport', 5);

-- Fiat (marca_id = 6)
INSERT INTO modelo (nome, marca_id) VALUES ('Argo', 6);
INSERT INTO modelo (nome, marca_id) VALUES ('Pulse', 6);
INSERT INTO modelo (nome, marca_id) VALUES ('Strada', 6);

-- Hyundai (marca_id = 7)
INSERT INTO modelo (nome, marca_id) VALUES ('HB20', 7);
INSERT INTO modelo (nome, marca_id) VALUES ('Creta', 7);
INSERT INTO modelo (nome, marca_id) VALUES ('Tucson', 7);

-- Renault (marca_id = 8)
INSERT INTO modelo (nome, marca_id) VALUES ('Kwid', 8);
INSERT INTO modelo (nome, marca_id) VALUES ('Sandero', 8);
INSERT INTO modelo (nome, marca_id) VALUES ('Duster', 8);

-- Nissan (marca_id = 9)
INSERT INTO modelo (nome, marca_id) VALUES ('Kicks', 9);
INSERT INTO modelo (nome, marca_id) VALUES ('Frontier', 9);
INSERT INTO modelo (nome, marca_id) VALUES ('Versa', 9);

-- BMW (marca_id = 10)
INSERT INTO modelo (nome, marca_id) VALUES ('Serie 3', 10);
INSERT INTO modelo (nome, marca_id) VALUES ('Serie 5', 10);
INSERT INTO modelo (nome, marca_id) VALUES ('X5', 10);


-- Clientes
INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('Maria Oliveira', 'Marcos Antônio Oliveira', 'CPF', '55563271064', 'maria.oliveira@email.com', '11988887777', true, 'Avenida Brasil', '456', 'Casa', 'Jardim América', 'São Paulo', 'SP', '01432000');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('Douglas Pereira', 'Jonathan Douglas Pereira', 'CPF', '84673421027', 'carlos.pereira@email.com', '21999998888', true, 'Rua do Sol', '789', 'Sala 5', 'Copacabana', 'Rio de Janeiro', 'RJ', '22041001');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('João Silva', 'João Silva', 'CPF', '93364249040', 'joao.silva@email.com', '47999990001', true, 'Rua das Flores', '123', 'Apto 1', 'Centro', 'Blumenau', 'SC', '89010000');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('Oficina ABC', 'Oficina e Comércio de Peças Automotivas ABC Ltda', 'CNPJ', '72781890000127', 'contato@oficinaabc.com.br', '47988880002', false, 'Av. Brasil', '456', null, 'Velha', 'Blumenau', 'SC', '89036000');


-- Veículo para Maria Oliveira (cliente_id = 1) — Toyota Corolla (modelo_id = 1)
INSERT INTO veiculo (placa, ano, cor, modelo_id, cliente_id)
VALUES ('ABC1A23', 2020, 'Prata', 1, 1);

-- Veículo para Douglas Pereira (cliente_id = 2) — Honda Civic (modelo_id = 4)
INSERT INTO veiculo (placa, ano, cor, modelo_id, cliente_id)
VALUES ('DEF2B34', 2019, 'Preto', 4, 2);

-- Veículo para João Silva (cliente_id = 3) — VW Gol (modelo_id = 7)
INSERT INTO veiculo (placa, ano, cor, modelo_id, cliente_id)
VALUES ('GHI3C45', 2021, 'Branco', 7, 3);

-- Veículo para Oficina ABC (cliente_id = 4) — VW Spacefox (modelo_id = 10)
INSERT INTO veiculo (placa, ano, cor, modelo_id, cliente_id)
VALUES ('JKL4D56', 2011, 'Vermelho', 10, 4);


-- =============================================================
-- PEÇAS
-- Produto IDs: 1..8
-- =============================================================

-- id=1: Filtro de óleo
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FILTRO_OLEO_001', '7891342010177', 'Filtro de Óleo Motor Universal', 32.90, 80, 0, 20, 'PECA', 'FILTRO_OLEO', NULL, NULL, NULL);

-- id=2: Filtro de combustível
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FILTRO_COMB_001', '7891342020177', 'Filtro de Combustível Universal', 28.50, 60, 0, 15, 'PECA', 'FILTRO_COMBUSTIVEL', NULL, NULL, NULL);

-- id=3: Pastilha de freio dianteira
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('PASTILHA_FREIO_001', '7891342030177', 'Pastilha de Freio Dianteira (jogo)', 89.90, 40, 0, 10, 'PECA', 'PASTILHA_FREIO', NULL, NULL, NULL);

-- id=4: Disco de freio dianteiro
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('DISCO_FREIO_001', '7891342040177', 'Disco de Freio Dianteiro (unidade)', 149.90, 30, 0, 8, 'PECA', 'DISCO_FREIO', NULL, NULL, NULL);

-- id=5: Correia dentada
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('CORREIA_DENT_001', '7891342050177', 'Correia Dentada Universal', 95.00, 25, 0, 8, 'PECA', 'CORREIA_DENTADA', NULL, NULL, NULL);

-- id=6: Tensor de correia
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('TENSOR_CORREIA_001', '7891342060177', 'Tensor de Correia Dentada', 75.00, 20, 0, 6, 'PECA', 'TENSOR_CORREIA', NULL, NULL, NULL);


-- =============================================================
-- INSUMOS
-- Produto IDs: 7..13
-- =============================================================

-- id=7: Óleo do motor 5W-30
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('OLEO_MOTOR_5W30', '5011987860575', 'Óleo de Motor 5W-30 Semissintético 1L', 38.00, 200, 0, 40, 'INSUMO', NULL, 'OLEO_MOTOR', 1.0, 'L');

-- id=8: Fluido de freio DOT 4
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FLUIDO_FREIO_DOT4', '7891106010014', 'Fluido de Freio DOT 4 500ml', 22.00, 100, 0, 20, 'INSUMO', NULL, 'FLUIDO_FREIO', 0.5, 'L');

-- id=9: Desengraxante
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('DESENGRAXANTE_001', '7896007512345', 'Desengraxante Automotivo 500ml', 18.00, 80, 0, 15, 'INSUMO', NULL, 'DESENGRAXANTE', 0.5, 'L');

-- id=10: Limpa-contato
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('LIMPA_CONTATO_001', '7896007512346', 'Limpa Contato Elétrico 300ml', 24.00, 60, 0, 10, 'INSUMO', NULL, 'LIMPA_CONTATO', 0.3, 'L');

-- id=11: Graxa
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('GRAXA_001', '7896007512347', 'Graxa de Uso Geral 200g', 15.00, 50, 0, 10, 'INSUMO', NULL, 'GRAXA', 0.2, 'L');


-- =============================================================
-- PEÇAS UNIVERSAIS
-- Produto IDs: 12..17
-- =============================================================

-- id=12: Filtro de óleo
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FILTRO_OLEO_002', '7891342070177', 'Filtro de Óleo Motor Universal', 32.90, 80, 0, 20, 'PECA', 'FILTRO_OLEO', NULL, NULL, NULL);

-- id=13: Filtro de combustível
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FILTRO_COMB_002', '7891342080177', 'Filtro de Combustível Universal', 28.50, 60, 0, 15, 'PECA', 'FILTRO_COMBUSTIVEL', NULL, NULL, NULL);

-- id=14: Pastilha de freio dianteira
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('PASTILHA_FREIO_002', '7891342090177', 'Pastilha de Freio Dianteira (jogo)', 89.90, 40, 0, 10, 'PECA', 'PASTILHA_FREIO', NULL, NULL, NULL);

-- id=15: Disco de freio dianteiro
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('DISCO_FREIO_002', '7891342100177', 'Disco de Freio Dianteiro (unidade)', 149.90, 30, 0, 8, 'PECA', 'DISCO_FREIO', NULL, NULL, NULL);

-- id=16: Correia dentada
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('CORREIA_DENT_002', '7891342110177', 'Correia Dentada Universal', 95.00, 25, 0, 8, 'PECA', 'CORREIA_DENTADA', NULL, NULL, NULL);

-- id=17: Tensor de correia
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('TENSOR_CORREIA_002', '7891342120177', 'Tensor de Correia Dentada', 75.00, 20, 0, 6, 'PECA', 'TENSOR_CORREIA', NULL, NULL, NULL);

-- =============================================================
-- INSUMOS
-- Produto IDs: 18..22
-- =============================================================

-- id=18: Óleo do motor 15W-40
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('OLEO_MOTOR_15W40', '50119878605751', 'Óleo de Motor 15W-40 Semissintético 1L', 38.00, 200, 0, 40, 'INSUMO', NULL, 'OLEO_MOTOR', 1.0, 'L');

-- id=19: Fluido de freio DOT 4
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('FLUIDO_FREIO_DOT5', '78911060100141', 'Fluido de Freio DOT 5 500ml', 22.00, 100, 0, 20, 'INSUMO', NULL, 'FLUIDO_FREIO', 0.5, 'L');

-- id=20: Desengraxante
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('DESENGRAXANTE_002', '78960075123451', 'Desengraxante Automotivo 500ml', 18.00, 80, 0, 15, 'INSUMO', NULL, 'DESENGRAXANTE', 0.5, 'L');

-- id=21: Limpa-contato
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('LIMPA_CONTATO_002', '78960075123461', 'Limpa Contato Elétrico 300ml', 24.00, 60, 0, 10, 'INSUMO', NULL, 'LIMPA_CONTATO', 0.3, 'L');

-- id=22: Graxa
INSERT INTO produtos (sku, ean, nome, preco, estoque, estoque_reservado, estoque_minimo, tipo_produto, tipo_peca, tipo_insumo, quantidade_embalagem, unidade_de_medida)
VALUES ('GRAXA_002', '78960075123471', 'Graxa de Uso Geral 200g', 15.00, 50, 0, 10, 'INSUMO', NULL, 'GRAXA', 0.2, 'L');


-- =============================================================
-- APLICAÇÕES DE PRODUTOS
-- =============================================================

-- Filtro de óleo
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (1, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (1, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (1, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (1, 10, 1, 2007, 2024);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (12, 2, 1, 1990, 2026),
                                                                                            (12, 3, 1, 1990, 2026),
                                                                                            (12, 5, 1, 1990, 2026),
                                                                                            (12, 6, 1, 1990, 2026),
                                                                                            (12, 8, 1, 1990, 2026),
                                                                                            (12, 9, 1, 1990, 2026),
                                                                                            (12, 11, 1, 1990, 2026),
                                                                                            (12, 12, 1, 1990, 2026),
                                                                                            (12, 13, 1, 1990, 2026),
                                                                                            (12, 14, 1, 1990, 2026),
                                                                                            (12, 15, 1, 1990, 2026),
                                                                                            (12, 16, 1, 1990, 2026),
                                                                                            (12, 17, 1, 1990, 2026),
                                                                                            (12, 18, 1, 1990, 2026),
                                                                                            (12, 19, 1, 1990, 2026),
                                                                                            (12, 20, 1, 1990, 2026),
                                                                                            (12, 21, 1, 1990, 2026),
                                                                                            (12, 22, 1, 1990, 2026),
                                                                                            (12, 23, 1, 1990, 2026),
                                                                                            (12, 24, 1, 1990, 2026),
                                                                                            (12, 25, 1, 1990, 2026),
                                                                                            (12, 26, 1, 1990, 2026),
                                                                                            (12, 27, 1, 1990, 2026),
                                                                                            (12, 28, 1, 1990, 2026),
                                                                                            (12, 29, 1, 1990, 2026),
                                                                                            (12, 30, 1, 1990, 2026),
                                                                                            (12, 31, 1, 1990, 2026);


-- Filtro de combustível
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (2, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (2, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (2, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (2, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (13, 2, 1, 1990, 2026),
                                                                                            (13, 3, 1, 1990, 2026),
                                                                                            (13, 5, 1, 1990, 2026),
                                                                                            (13, 6, 1, 1990, 2026),
                                                                                            (13, 8, 1, 1990, 2026),
                                                                                            (13, 9, 1, 1990, 2026),
                                                                                            (13, 11, 1, 1990, 2026),
                                                                                            (13, 12, 1, 1990, 2026),
                                                                                            (13, 13, 1, 1990, 2026),
                                                                                            (13, 14, 1, 1990, 2026),
                                                                                            (13, 15, 1, 1990, 2026),
                                                                                            (13, 16, 1, 1990, 2026),
                                                                                            (13, 17, 1, 1990, 2026),
                                                                                            (13, 18, 1, 1990, 2026),
                                                                                            (13, 19, 1, 1990, 2026),
                                                                                            (13, 20, 1, 1990, 2026),
                                                                                            (13, 21, 1, 1990, 2026),
                                                                                            (13, 22, 1, 1990, 2026),
                                                                                            (13, 23, 1, 1990, 2026),
                                                                                            (13, 24, 1, 1990, 2026),
                                                                                            (13, 25, 1, 1990, 2026),
                                                                                            (13, 26, 1, 1990, 2026),
                                                                                            (13, 27, 1, 1990, 2026),
                                                                                            (13, 28, 1, 1990, 2026),
                                                                                            (13, 29, 1, 1990, 2026),
                                                                                            (13, 30, 1, 1990, 2026),
                                                                                            (13, 31, 1, 1990, 2026);

-- Pastilha de freio
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (3, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (3, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (3, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (3, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (14, 2, 1, 1990, 2026),
                                                                                            (14, 3, 1, 1990, 2026),
                                                                                            (14, 5, 1, 1990, 2026),
                                                                                            (14, 6, 1, 1990, 2026),
                                                                                            (14, 8, 1, 1990, 2026),
                                                                                            (14, 9, 1, 1990, 2026),
                                                                                            (14, 11, 1, 1990, 2026),
                                                                                            (14, 12, 1, 1990, 2026),
                                                                                            (14, 13, 1, 1990, 2026),
                                                                                            (14, 14, 1, 1990, 2026),
                                                                                            (14, 15, 1, 1990, 2026),
                                                                                            (14, 16, 1, 1990, 2026),
                                                                                            (14, 17, 1, 1990, 2026),
                                                                                            (14, 18, 1, 1990, 2026),
                                                                                            (14, 19, 1, 1990, 2026),
                                                                                            (14, 20, 1, 1990, 2026),
                                                                                            (14, 21, 1, 1990, 2026),
                                                                                            (14, 22, 1, 1990, 2026),
                                                                                            (14, 23, 1, 1990, 2026),
                                                                                            (14, 24, 1, 1990, 2026),
                                                                                            (14, 25, 1, 1990, 2026),
                                                                                            (14, 26, 1, 1990, 2026),
                                                                                            (14, 27, 1, 1990, 2026),
                                                                                            (14, 28, 1, 1990, 2026),
                                                                                            (14, 29, 1, 1990, 2026),
                                                                                            (14, 30, 1, 1990, 2026),
                                                                                            (14, 31, 1, 1990, 2026);

-- Disco de freio
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (4, 1, 2, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (4, 4, 2, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (4, 7, 2, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (4, 10, 2, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (15, 2, 1, 1990, 2026),
                                                                                            (15, 3, 1, 1990, 2026),
                                                                                            (15, 5, 1, 1990, 2026),
                                                                                            (15, 6, 1, 1990, 2026),
                                                                                            (15, 8, 1, 1990, 2026),
                                                                                            (15, 9, 1, 1990, 2026),
                                                                                            (15, 11, 1, 1990, 2026),
                                                                                            (15, 12, 1, 1990, 2026),
                                                                                            (15, 13, 1, 1990, 2026),
                                                                                            (15, 14, 1, 1990, 2026),
                                                                                            (15, 15, 1, 1990, 2026),
                                                                                            (15, 16, 1, 1990, 2026),
                                                                                            (15, 17, 1, 1990, 2026),
                                                                                            (15, 18, 1, 1990, 2026),
                                                                                            (15, 19, 1, 1990, 2026),
                                                                                            (15, 20, 1, 1990, 2026),
                                                                                            (15, 21, 1, 1990, 2026),
                                                                                            (15, 22, 1, 1990, 2026),
                                                                                            (15, 23, 1, 1990, 2026),
                                                                                            (15, 24, 1, 1990, 2026),
                                                                                            (15, 25, 1, 1990, 2026),
                                                                                            (15, 26, 1, 1990, 2026),
                                                                                            (15, 27, 1, 1990, 2026),
                                                                                            (15, 28, 1, 1990, 2026),
                                                                                            (15, 29, 1, 1990, 2026),
                                                                                            (15, 30, 1, 1990, 2026),
                                                                                            (15, 31, 1, 1990, 2026);

-- Correia dentada
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (5, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (5, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (5, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (5, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (16, 2, 1, 1990, 2026),
                                                                                            (16, 3, 1, 1990, 2026),
                                                                                            (16, 5, 1, 1990, 2026),
                                                                                            (16, 6, 1, 1990, 2026),
                                                                                            (16, 8, 1, 1990, 2026),
                                                                                            (16, 9, 1, 1990, 2026),
                                                                                            (16, 11, 1, 1990, 2026),
                                                                                            (16, 12, 1, 1990, 2026),
                                                                                            (16, 13, 1, 1990, 2026),
                                                                                            (16, 14, 1, 1990, 2026),
                                                                                            (16, 15, 1, 1990, 2026),
                                                                                            (16, 16, 1, 1990, 2026),
                                                                                            (16, 17, 1, 1990, 2026),
                                                                                            (16, 18, 1, 1990, 2026),
                                                                                            (16, 19, 1, 1990, 2026),
                                                                                            (16, 20, 1, 1990, 2026),
                                                                                            (16, 21, 1, 1990, 2026),
                                                                                            (16, 22, 1, 1990, 2026),
                                                                                            (16, 23, 1, 1990, 2026),
                                                                                            (16, 24, 1, 1990, 2026),
                                                                                            (16, 25, 1, 1990, 2026),
                                                                                            (16, 26, 1, 1990, 2026),
                                                                                            (16, 27, 1, 1990, 2026),
                                                                                            (16, 28, 1, 1990, 2026),
                                                                                            (16, 29, 1, 1990, 2026),
                                                                                            (16, 30, 1, 1990, 2026),
                                                                                            (16, 31, 1, 1990, 2026);

-- Tensor de correia
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (6, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (6, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (6, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (6, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (17, 2, 1, 1990, 2026),
                                                                                            (17, 3, 1, 1990, 2026),
                                                                                            (17, 5, 1, 1990, 2026),
                                                                                            (17, 6, 1, 1990, 2026),
                                                                                            (17, 8, 1, 1990, 2026),
                                                                                            (17, 9, 1, 1990, 2026),
                                                                                            (17, 11, 1, 1990, 2026),
                                                                                            (17, 12, 1, 1990, 2026),
                                                                                            (17, 13, 1, 1990, 2026),
                                                                                            (17, 14, 1, 1990, 2026),
                                                                                            (17, 15, 1, 1990, 2026),
                                                                                            (17, 16, 1, 1990, 2026),
                                                                                            (17, 17, 1, 1990, 2026),
                                                                                            (17, 18, 1, 1990, 2026),
                                                                                            (17, 19, 1, 1990, 2026),
                                                                                            (17, 20, 1, 1990, 2026),
                                                                                            (17, 21, 1, 1990, 2026),
                                                                                            (17, 22, 1, 1990, 2026),
                                                                                            (17, 23, 1, 1990, 2026),
                                                                                            (17, 24, 1, 1990, 2026),
                                                                                            (17, 25, 1, 1990, 2026),
                                                                                            (17, 26, 1, 1990, 2026),
                                                                                            (17, 27, 1, 1990, 2026),
                                                                                            (17, 28, 1, 1990, 2026),
                                                                                            (17, 29, 1, 1990, 2026),
                                                                                            (17, 30, 1, 1990, 2026),
                                                                                            (17, 31, 1, 1990, 2026);

-- Óleo do motor 5W-30
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (7, 1, 4, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (7, 4, 4, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (7, 7, 4, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (7, 10, 4, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (18, 2, 4, 1990, 2026),
                                                                                            (18, 3, 4, 1990, 2026),
                                                                                            (18, 5, 4, 1990, 2026),
                                                                                            (18, 6, 4, 1990, 2026),
                                                                                            (18, 8, 4, 1990, 2026),
                                                                                            (18, 9, 4, 1990, 2026),
                                                                                            (18, 11, 4, 1990, 2026),
                                                                                            (18, 12, 4, 1990, 2026),
                                                                                            (18, 13, 4, 1990, 2026),
                                                                                            (18, 14, 4, 1990, 2026),
                                                                                            (18, 15, 4, 1990, 2026),
                                                                                            (18, 16, 4, 1990, 2026),
                                                                                            (18, 17, 4, 1990, 2026),
                                                                                            (18, 18, 4, 1990, 2026),
                                                                                            (18, 19, 4, 1990, 2026),
                                                                                            (18, 20, 4, 1990, 2026),
                                                                                            (18, 21, 4, 1990, 2026),
                                                                                            (18, 22, 4, 1990, 2026),
                                                                                            (18, 23, 4, 1990, 2026),
                                                                                            (18, 24, 4, 1990, 2026),
                                                                                            (18, 25, 4, 1990, 2026),
                                                                                            (18, 26, 4, 1990, 2026),
                                                                                            (18, 27, 4, 1990, 2026),
                                                                                            (18, 28, 4, 1990, 2026),
                                                                                            (18, 29, 4, 1990, 2026),
                                                                                            (18, 30, 4, 1990, 2026),
                                                                                            (18, 31, 4, 1990, 2026);

-- Fluido de freio DOT 4
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (8, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (8, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (8, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (8, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (19, 2, 1, 1990, 2026),
                                                                                            (19, 3, 1, 1990, 2026),
                                                                                            (19, 5, 1, 1990, 2026),
                                                                                            (19, 6, 1, 1990, 2026),
                                                                                            (19, 8, 1, 1990, 2026),
                                                                                            (19, 9, 1, 1990, 2026),
                                                                                            (19, 11, 1, 1990, 2026),
                                                                                            (19, 12, 1, 1990, 2026),
                                                                                            (19, 13, 1, 1990, 2026),
                                                                                            (19, 14, 1, 1990, 2026),
                                                                                            (19, 15, 1, 1990, 2026),
                                                                                            (19, 16, 1, 1990, 2026),
                                                                                            (19, 17, 1, 1990, 2026),
                                                                                            (19, 18, 1, 1990, 2026),
                                                                                            (19, 19, 1, 1990, 2026),
                                                                                            (19, 20, 1, 1990, 2026),
                                                                                            (19, 21, 1, 1990, 2026),
                                                                                            (19, 22, 1, 1990, 2026),
                                                                                            (19, 23, 1, 1990, 2026),
                                                                                            (19, 24, 1, 1990, 2026),
                                                                                            (19, 25, 1, 1990, 2026),
                                                                                            (19, 26, 1, 1990, 2026),
                                                                                            (19, 27, 1, 1990, 2026),
                                                                                            (19, 28, 1, 1990, 2026),
                                                                                            (19, 29, 1, 1990, 2026),
                                                                                            (19, 30, 1, 1990, 2026),
                                                                                            (19, 31, 1, 1990, 2026);

-- Desengraxante
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (9, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (9, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (9, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (9, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (20, 2, 1, 1990, 2026),
                                                                                            (20, 3, 1, 1990, 2026),
                                                                                            (20, 5, 1, 1990, 2026),
                                                                                            (20, 6, 1, 1990, 2026),
                                                                                            (20, 8, 1, 1990, 2026),
                                                                                            (20, 9, 1, 1990, 2026),
                                                                                            (20, 11, 1, 1990, 2026),
                                                                                            (20, 12, 1, 1990, 2026),
                                                                                            (20, 13, 1, 1990, 2026),
                                                                                            (20, 14, 1, 1990, 2026),
                                                                                            (20, 15, 1, 1990, 2026),
                                                                                            (20, 16, 1, 1990, 2026),
                                                                                            (20, 17, 1, 1990, 2026),
                                                                                            (20, 18, 1, 1990, 2026),
                                                                                            (20, 19, 1, 1990, 2026),
                                                                                            (20, 20, 1, 1990, 2026),
                                                                                            (20, 21, 1, 1990, 2026),
                                                                                            (20, 22, 1, 1990, 2026),
                                                                                            (20, 23, 1, 1990, 2026),
                                                                                            (20, 24, 1, 1990, 2026),
                                                                                            (20, 25, 1, 1990, 2026),
                                                                                            (20, 26, 1, 1990, 2026),
                                                                                            (20, 27, 1, 1990, 2026),
                                                                                            (20, 28, 1, 1990, 2026),
                                                                                            (20, 29, 1, 1990, 2026),
                                                                                            (20, 30, 1, 1990, 2026),
                                                                                            (20, 31, 1, 1990, 2026);

-- Limpa-contato
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (10, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (10, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (10, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (10, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (21, 2, 1, 1990, 2026),
                                                                                            (21, 3, 1, 1990, 2026),
                                                                                            (21, 5, 1, 1990, 2026),
                                                                                            (21, 6, 1, 1990, 2026),
                                                                                            (21, 8, 1, 1990, 2026),
                                                                                            (21, 9, 1, 1990, 2026),
                                                                                            (21, 11, 1, 1990, 2026),
                                                                                            (21, 12, 1, 1990, 2026),
                                                                                            (21, 13, 1, 1990, 2026),
                                                                                            (21, 14, 1, 1990, 2026),
                                                                                            (21, 15, 1, 1990, 2026),
                                                                                            (21, 16, 1, 1990, 2026),
                                                                                            (21, 17, 1, 1990, 2026),
                                                                                            (21, 18, 1, 1990, 2026),
                                                                                            (21, 19, 1, 1990, 2026),
                                                                                            (21, 20, 1, 1990, 2026),
                                                                                            (21, 21, 1, 1990, 2026),
                                                                                            (21, 22, 1, 1990, 2026),
                                                                                            (21, 23, 1, 1990, 2026),
                                                                                            (21, 24, 1, 1990, 2026),
                                                                                            (21, 25, 1, 1990, 2026),
                                                                                            (21, 26, 1, 1990, 2026),
                                                                                            (21, 27, 1, 1990, 2026),
                                                                                            (21, 28, 1, 1990, 2026),
                                                                                            (21, 29, 1, 1990, 2026),
                                                                                            (21, 30, 1, 1990, 2026),
                                                                                            (21, 31, 1, 1990, 2026);

-- Graxa
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (11, 1, 1, 2014, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (11, 4, 1, 2012, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (11, 7, 1, 2008, 2024);
INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES (11, 10, 1, 2007, 2017);

INSERT INTO aplicacao_produtos (produto_id, modelo_id, quantidade, ano_inicio, ano_fim) VALUES
                                                                                            (22, 2, 1, 1990, 2026),
                                                                                            (22, 3, 1, 1990, 2026),
                                                                                            (22, 5, 1, 1990, 2026),
                                                                                            (22, 6, 1, 1990, 2026),
                                                                                            (22, 8, 1, 1990, 2026),
                                                                                            (22, 9, 1, 1990, 2026),
                                                                                            (22, 11, 1, 1990, 2026),
                                                                                            (22, 12, 1, 1990, 2026),
                                                                                            (22, 13, 1, 1990, 2026),
                                                                                            (22, 14, 1, 1990, 2026),
                                                                                            (22, 15, 1, 1990, 2026),
                                                                                            (22, 16, 1, 1990, 2026),
                                                                                            (22, 17, 1, 1990, 2026),
                                                                                            (22, 18, 1, 1990, 2026),
                                                                                            (22, 19, 1, 1990, 2026),
                                                                                            (22, 20, 1, 1990, 2026),
                                                                                            (22, 21, 1, 1990, 2026),
                                                                                            (22, 22, 1, 1990, 2026),
                                                                                            (22, 23, 1, 1990, 2026),
                                                                                            (22, 24, 1, 1990, 2026),
                                                                                            (22, 25, 1, 1990, 2026),
                                                                                            (22, 26, 1, 1990, 2026),
                                                                                            (22, 27, 1, 1990, 2026),
                                                                                            (22, 28, 1, 1990, 2026),
                                                                                            (22, 29, 1, 1990, 2026),
                                                                                            (22, 30, 1, 1990, 2026),
                                                                                            (22, 31, 1, 1990, 2026);


-- =============================================================
-- SERVIÇOS
-- Tabelas: servicos, servico_insumos, servico_tipo_pecas
-- =============================================================

-- Serviço 1: Troca de óleo do motor
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de óleo do motor', 0.75);
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (1, 'OLEO_MOTOR');
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (1, 'FILTRO_OLEO', 1);

-- Serviço 2: Troca de filtro de combustível
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de filtro de combustível', 0.5);
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (2, 'FILTRO_COMBUSTIVEL', 1);

-- Serviço 3: Limpeza de bicos injetores
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Limpeza de bicos injetores', 2.0);
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (3, 'DESENGRAXANTE');
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (3, 'LIMPA_CONTATO');

-- Serviço 4: Troca de pastilhas de freio
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de pastilhas de freio', 1.0);
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (4, 'PASTILHA_FREIO', 1);

-- Serviço 5: Troca de discos de freio
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de discos de freio', 1.5);
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (5, 'DISCO_FREIO', 2);

-- Serviço 6: Sangria do sistema de freio
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Sangria do sistema de freio', 1.0);
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (6, 'FLUIDO_FREIO');

-- Serviço 7: Troca de fluido de freio
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de fluido de freio', 0.75);
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (7, 'FLUIDO_FREIO');

-- Serviço 8: Troca de correia dentada
INSERT INTO servicos (nome, horas_tecnicas) VALUES ('Troca de correia dentada', 3.0);
INSERT INTO servico_insumos (servico_id, tipo_insumo) VALUES (8, 'GRAXA');
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (8, 'CORREIA_DENTADA', 1);
INSERT INTO servico_tipo_pecas (servico_id, tipo_peca, quantidade) VALUES (8, 'TENSOR_CORREIA', 1);