-- Demo data for /adocoes using login cpf 33333333333 (Joao Silva).
-- Keeps all three tabs populated: active as donor, history, rejected pets.

UPDATE pet
SET status_aprovacao = 'APROVADO', adm_aprovou = '11111111111'
WHERE (nome, cpf_dono) IN (
    ('Rex', '33333333333'),
    ('Mimi', '33333333333'),
    ('Bidu', '44444444444'),
    ('Nina', '44444444444')
);

INSERT INTO pet (
    nome, cpf_dono, bio, sexo, tipo_pet, data_nasc, data_cadastro, porte,
    is_permanente, is_castrado, adm_aprovou, status_aprovacao
) VALUES
    ('Toby', '33333333333', 'Cachorro jovem, pedido negado por dados incompletos', 'M', 1, '2023-02-11', '2024-01-20', 'M', true, false, '22222222222', 'REJEITADO')
ON CONFLICT (nome, cpf_dono) DO UPDATE
SET status_aprovacao = EXCLUDED.status_aprovacao,
    adm_aprovou = EXCLUDED.adm_aprovou;

INSERT INTO adocao (
    data_inicio, cpf_adotante, pet_nome, pet_dono, data_fim,
    data_solicitacao, status, is_permanente
) VALUES
    ('2024-02-01', '44444444444', 'Rex', '33333333333', NULL, '2024-01-25', 'EM_ANDAMENTO', true),
    ('2023-08-05', '55555555555', 'Mimi', '33333333333', '2023-11-05', '2023-07-22', 'CONCLUIDA', false),
    ('2023-12-10', '10000000015', 'Mimi', '33333333333', '2024-01-15', '2023-12-02', 'CANCELADA', false)
ON CONFLICT (data_inicio, cpf_adotante, pet_nome, pet_dono) DO NOTHING;

INSERT INTO devolucao (
    adocao_inicio, adocao_adotante, adocao_pet, adocao_dono,
    motivo, data_solicitacao
) VALUES
    ('2024-02-01', '44444444444', 'Rex', '33333333333', 'Adotante solicitou devolucao por mudanca de cidade', '2024-03-01')
ON CONFLICT (adocao_inicio, adocao_adotante, adocao_pet, adocao_dono) DO NOTHING;
