CREATE DATABASE IF NOT EXISTS tvmaze_catalog;
USE tvmaze_catalog;

CREATE TABLE IF NOT EXISTS serie (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_externo INT UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    linguagem VARCHAR(50),
    status VARCHAR(50),
    sinopse TEXT,
    nota_media DECIMAL(3,1),
    data_estreia DATE,
    data_termino DATE,
    observacao TEXT,
    status_local VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS genero (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS serie_genero (
    serie_id INT,
    genero_id INT,
    PRIMARY KEY (serie_id, genero_id),
    FOREIGN KEY (serie_id) REFERENCES serie(id) ON DELETE CASCADE,
    FOREIGN KEY (genero_id) REFERENCES genero(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS episodio (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_externo INT UNIQUE NOT NULL,
    serie_id INT NOT NULL,
    temporada INT,
    numero INT,
    nome VARCHAR(255),
    data_exibicao DATE,
    duracao INT,
    FOREIGN KEY (serie_id) REFERENCES serie(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS pessoa (
    id INT PRIMARY KEY AUTO_INCREMENT,
    id_externo INT UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    pais VARCHAR(100),
    data_nascimento DATE
);

CREATE TABLE IF NOT EXISTS participacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    serie_id INT NOT NULL,
    pessoa_id INT NOT NULL,
    personagem VARCHAR(255),
    FOREIGN KEY (serie_id) REFERENCES serie(id) ON DELETE CASCADE,
    FOREIGN KEY (pessoa_id) REFERENCES pessoa(id) ON DELETE CASCADE,
    UNIQUE KEY unique_participation (serie_id, pessoa_id, personagem)
);

CREATE TABLE IF NOT EXISTS log_operacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operacao VARCHAR(50),
    entidade VARCHAR(50),
    descricao TEXT,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);