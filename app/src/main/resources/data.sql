INSERT INTO marca (id, nome) VALUES (1, 'Toyota');
INSERT INTO marca (id, nome) VALUES (2, 'Honda');
INSERT INTO marca (id, nome) VALUES (3, 'Volkswagen');
INSERT INTO marca (id, nome) VALUES (4, 'Chevrolet');
INSERT INTO marca (id, nome) VALUES (5, 'Ford');
INSERT INTO marca (id, nome) VALUES (6, 'Fiat');
INSERT INTO marca (id, nome) VALUES (7, 'Hyundai');
INSERT INTO marca (id, nome) VALUES (8, 'Renault');
INSERT INTO marca (id, nome) VALUES (9, 'Nissan');
INSERT INTO marca (id, nome) VALUES (10, 'BMW');

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
INSERT INTO clientes (id, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES (1, 'João Silva', 'CPF', '123.456.789-00', 'joao.silva@email.com', '47999990001', true, 'Rua das Flores', '123', 'Apto 1', 'Centro', 'Blumenau', 'SC', '89010-000');

INSERT INTO clientes (id, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES (2, 'Oficina ABC Ltda', 'CNPJ', '12.345.678/0001-99', 'contato@oficinaabc.com.br', '47988880002', false, 'Av. Brasil', '456', null, 'Velha', 'Blumenau', 'SC', '89036-000');