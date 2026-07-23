CREATE TYPE nivel_enum AS ENUM ('M', 'S');
CREATE TYPE genero_enum AS ENUM ('M', 'F', 'O');
CREATE TYPE sexo_enum AS ENUM ('M', 'F');
CREATE TYPE porte_enum AS ENUM ('P', 'M', 'G');
CREATE TYPE status_enum AS ENUM ('EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA');
CREATE TYPE status_aprovacao_enum AS ENUM ('PENDENTE', 'APROVADO', 'REJEITADO');

CREATE TABLE pessoa (
    cpf CHAR(11) PRIMARY KEY,
    nome VARCHAR(45),
    email VARCHAR(254),
    senha VARCHAR(255)
);

CREATE TABLE administrador (
    cpf CHAR(11) PRIMARY KEY,
    nivel nivel_enum,
    CONSTRAINT fk_admin_pessoa
        FOREIGN KEY (cpf)
        REFERENCES pessoa (cpf)
);

CREATE TABLE usuario (
    cpf CHAR(11) PRIMARY KEY,
    username VARCHAR(45) NOT NULL UNIQUE,
    bio VARCHAR(255),
    genero genero_enum,
    telefone CHAR(11),
    foto_perfil VARCHAR(300),
    CONSTRAINT fk_usuario_pessoa
        FOREIGN KEY (cpf)
        REFERENCES pessoa (cpf)
);

CREATE TABLE tipo_pet (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_mae INTEGER,
    nome VARCHAR(50) NOT NULL,
    CONSTRAINT fk_tipo_mae
        FOREIGN KEY (tipo_mae)
        REFERENCES tipo_pet (id)
);

CREATE TABLE pet (
    nome VARCHAR(100) NOT NULL,
    cpf_dono CHAR(11) NOT NULL,
    bio VARCHAR(255),
    sexo sexo_enum,
    tipo_pet INTEGER NOT NULL,
    data_nasc DATE,
    data_cadastro DATE,
    porte porte_enum,
    is_permanente BOOLEAN,
    is_castrado BOOLEAN,
    adm_aprovou CHAR(11),
    foto_pet VARCHAR(300),
    status_aprovacao status_aprovacao_enum NOT NULL DEFAULT 'PENDENTE',
    PRIMARY KEY (nome, cpf_dono),

    CONSTRAINT fk_pet_adm
        FOREIGN KEY (adm_aprovou)
        REFERENCES administrador (cpf),

    CONSTRAINT fk_pet_dono
        FOREIGN KEY (cpf_dono)
        REFERENCES usuario (cpf),

    CONSTRAINT fk_pet_tipo
        FOREIGN KEY (tipo_pet)
        REFERENCES tipo_pet (id)
);
CREATE TABLE adocao (
    data_inicio DATE NOT NULL,
    cpf_adotante CHAR(11) NOT NULL,
    pet_nome VARCHAR(100) NOT NULL,
    pet_dono CHAR(11) NOT NULL,
    data_fim DATE,
    data_solicitacao DATE,
    status status_enum,
    is_permanente BOOLEAN,

    PRIMARY KEY (data_inicio, cpf_adotante, pet_nome, pet_dono),

    CONSTRAINT fk_adocao_adotante
        FOREIGN KEY (cpf_adotante)
        REFERENCES usuario (cpf),

    CONSTRAINT fk_adocao_pet
        FOREIGN KEY (pet_nome, pet_dono)
        REFERENCES pet (nome, cpf_dono)
);

CREATE TABLE devolucao (
    adocao_inicio DATE NOT NULL,
    adocao_adotante CHAR(11) NOT NULL,
    adocao_pet VARCHAR(100) NOT NULL,
    adocao_dono CHAR(11) NOT NULL,
    motivo VARCHAR(255),
    data_solicitacao DATE,

    PRIMARY KEY (
        adocao_inicio,
        adocao_adotante,
        adocao_pet,
        adocao_dono
    ),

    CONSTRAINT fk_devolucao_adocao
        FOREIGN KEY (
            adocao_inicio,
            adocao_adotante,
            adocao_pet,
            adocao_dono
        )
        REFERENCES adocao (
            data_inicio,
            cpf_adotante,
            pet_nome,
            pet_dono
        )
);

CREATE TABLE notificacao (
    pessoa_cpf CHAR(11) NOT NULL,
    data TIMESTAMP NOT NULL,
    mensagem VARCHAR(600),
    is_lido BOOLEAN,

    PRIMARY KEY (pessoa_cpf, data),

    CONSTRAINT fk_notificacao_pessoa
        FOREIGN KEY (pessoa_cpf)
        REFERENCES pessoa (cpf)
);

CREATE TABLE avaliacao (
    adocao_inicio DATE NOT NULL,
    adocao_adotante CHAR(11) NOT NULL,
    adocao_pet VARCHAR(100) NOT NULL,
    adocao_dono CHAR(11) NOT NULL,
    nota SMALLINT,
    comentario VARCHAR(255),
    data DATE,

    PRIMARY KEY (
        adocao_inicio,
        adocao_adotante,
        adocao_pet,
        adocao_dono
    ),

    CONSTRAINT fk_avaliacao_adocao
        FOREIGN KEY (
            adocao_inicio,
            adocao_adotante,
            adocao_pet,
            adocao_dono
        )
        REFERENCES adocao (
            data_inicio,
            cpf_adotante,
            pet_nome,
            pet_dono
        ),

    CONSTRAINT chk_nota
        CHECK (nota BETWEEN 0 AND 255)
);

CREATE TABLE endereco (
    usuario_cpf CHAR(11) PRIMARY KEY,
    cep CHAR(8),
    rua VARCHAR(150),
    numero VARCHAR(6),
    complemento VARCHAR(45),
    bairro VARCHAR(45),
    cidade VARCHAR(35),
    uf CHAR(2),

    CONSTRAINT fk_endereco_usuario
        FOREIGN KEY (usuario_cpf)
        REFERENCES usuario (cpf)
);

CREATE TABLE saude_pet (
    pet_nome VARCHAR(100) NOT NULL,
    pet_dono CHAR(11) NOT NULL,
    usa_medicamento BOOLEAN,
    desc_medicamento VARCHAR(255),
    condicao_especial BOOLEAN,
    desc_cuidados VARCHAR(255),

    PRIMARY KEY (pet_nome, pet_dono),

    CONSTRAINT fk_saude_pet
        FOREIGN KEY (pet_nome, pet_dono)
        REFERENCES pet (nome, cpf_dono)
);
