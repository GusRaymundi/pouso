-- Fotos de perfil dos doadores de teste
UPDATE usuario
SET foto_perfil = 'https://api.dicebear.com/9.x/initials/svg?seed=Carlos%20Doador'
WHERE cpf = '22222222222';

UPDATE usuario
SET foto_perfil = 'https://api.dicebear.com/9.x/initials/svg?seed=Victor%20Doador'
WHERE cpf = '33333333642';

-- Pets aprovados para aparecerem nos perfis publicos
UPDATE pet
SET status_aprovacao = 'APROVADO', adm_aprovou = '11111111111'
WHERE (nome, cpf_dono) IN (
    ('Rex', '22222222222'),
    ('Bob', '33333333642'),
    ('Thor', '33333333642'),
    ('Mel', '33333333642')
);

-- Adotantes ficticios para compor historico de adocoes e avaliacoes
INSERT INTO pessoa (cpf, nome, email, senha) VALUES
('44444444444', 'Marina Adotante', 'marina@pouso.com', 'senha123'),
('55555555555', 'Joao Adotante', 'joao@pouso.com', 'senha123'),
('66666666666', 'Paula Adotante', 'paula@pouso.com', 'senha123'),
('77777777777', 'Renata Adotante', 'renata@pouso.com', 'senha123');

INSERT INTO usuario (cpf, username, bio, genero, telefone, foto_perfil) VALUES
('44444444444', 'marinaa', 'Adotante apaixonada por animais tranquilos.', 'F', '51999990003', 'https://api.dicebear.com/9.x/initials/svg?seed=Marina%20Adotante'),
('55555555555', 'joaoadota', 'Tenho experiencia com caes idosos.', 'M', '51999990004', 'https://api.dicebear.com/9.x/initials/svg?seed=Joao%20Adotante'),
('66666666666', 'paulapets', 'Cuido de gatos e caes resgatados.', 'F', '51999990005', 'https://api.dicebear.com/9.x/initials/svg?seed=Paula%20Adotante'),
('77777777777', 'renataadota', 'Busco pets para lar temporario e permanente.', 'F', '51999990006', 'https://api.dicebear.com/9.x/initials/svg?seed=Renata%20Adotante');

INSERT INTO endereco (usuario_cpf, cep, rua, numero, complemento, bairro, cidade, uf) VALUES
('22222222222', '90010000', 'Rua dos Andradas', '1200', null, 'Centro Historico', 'Porto Alegre', 'RS'),
('33333333642', '90610000', 'Avenida Ipiranga', '6681', 'Casa 2', 'Partenon', 'Porto Alegre', 'RS'),
('44444444444', '90430000', 'Rua Padre Chagas', '85', null, 'Moinhos de Vento', 'Porto Alegre', 'RS'),
('55555555555', '90570020', 'Rua Dom Pedro II', '310', 'Apto 402', 'Sao Joao', 'Porto Alegre', 'RS'),
('66666666666', '91751000', 'Avenida Wenceslau Escobar', '1823', null, 'Tristeza', 'Porto Alegre', 'RS'),
('77777777777', '91330001', 'Avenida Assis Brasil', '4500', 'Bloco B', 'Sarandi', 'Porto Alegre', 'RS');

INSERT INTO adocao (
    data_inicio,
    cpf_adotante,
    pet_nome,
    pet_dono,
    data_fim,
    data_solicitacao,
    status,
    is_permanente
) VALUES
('2025-01-10', '44444444444', 'Rex', '22222222222', '2025-02-10', '2025-01-05', 'CONCLUIDA', true),
('2025-03-15', '55555555555', 'Rex', '22222222222', '2025-04-15', '2025-03-10', 'CONCLUIDA', false),
('2025-02-01', '66666666666', 'Bob', '33333333642', '2025-03-01', '2025-01-28', 'CONCLUIDA', true),
('2025-04-05', '77777777777', 'Thor', '33333333642', '2025-05-05', '2025-04-01', 'CONCLUIDA', true),
('2025-05-20', '44444444444', 'Mel', '33333333642', '2025-06-20', '2025-05-15', 'CONCLUIDA', false);

INSERT INTO avaliacao (
    adocao_inicio,
    adocao_adotante,
    adocao_pet,
    adocao_dono,
    nota,
    comentario,
    data
) VALUES
('2025-01-10', '44444444444', 'Rex', '22222222222', 5, 'Carlos foi muito atencioso, explicou a rotina do Rex e acompanhou toda a adaptacao.', '2025-02-11'),
('2025-03-15', '55555555555', 'Rex', '22222222222', 4, 'Boa experiencia. O Rex chegou bem cuidado e com todas as informacoes necessarias.', '2025-04-16'),
('2025-02-01', '66666666666', 'Bob', '33333333642', 5, 'Victor demonstrou muito carinho pelo Bob e facilitou todo o processo de adocao.', '2025-03-02'),
('2025-04-05', '77777777777', 'Thor', '33333333642', 5, 'Thor estava saudavel, vacinado e muito bem socializado. Excelente doador.', '2025-05-06'),
('2025-05-20', '44444444444', 'Mel', '33333333642', 4, 'Mel veio tranquila e bem cuidada. Victor respondeu rapido e orientou sobre os cuidados.', '2025-06-21');
