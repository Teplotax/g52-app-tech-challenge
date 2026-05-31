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
VALUES ('Maria Oliveira', 'Marcos Antônio Oliveira', 'CPF', '55563271064', 'maria.oliveira@email.com', '11988887777', false, 'Avenida Brasil', '456', 'Casa', 'Jardim América', 'São Paulo', 'SP', '01432000');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('Douglas Pereira', 'Jonathan Douglas Pereira', 'CPF', '84673421027', 'carlos.pereira@email.com', '21999998888', true, 'Rua do Sol', '789', 'Sala 5', 'Copacabana', 'Rio de Janeiro', 'RJ', '22041001');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('João Silva', 'João Silva', 'CPF', '93364249040', 'joao.silva@email.com', '47999990001', true, 'Rua das Flores', '123', 'Apto 1', 'Centro', 'Blumenau', 'SC', '89010000');

INSERT INTO clientes (nome_social, nome, tipo_documento, documento, email, telefone, contato_whats_app, logradouro, numero, complemento, bairro, cidade, uf, cep)
VALUES ('Oficina ABC', 'Oficina e Comércio de Peças Automotivas ABC Ltda', 'CNPJ', '72781890000127', 'contato@oficinaabc.com.br', '47988880002', false, 'Av. Brasil', '456', null, 'Velha', 'Blumenau', 'SC', '89036000');
