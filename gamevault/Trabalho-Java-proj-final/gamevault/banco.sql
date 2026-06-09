-- ============================================================
-- GameVault — Script de inicialização do banco de dados
-- PostgreSQL
-- ============================================================

-- 1. Criar banco (execute separado se necessário)
-- CREATE DATABASE gamevault;

-- 2. Tipo enum para perfil de usuário
CREATE TYPE tipo_usuario AS ENUM ('comum', 'admin');

-- 3. Tabela de usuários
CREATE TABLE IF NOT EXISTS usuarios (
    id     SERIAL PRIMARY KEY,
    login  VARCHAR(60)  NOT NULL UNIQUE,
    senha  VARCHAR(255) NOT NULL,
    tipo   tipo_usuario NOT NULL DEFAULT 'comum'
);

-- 4. Tabela de gêneros de jogo (entidade principal)
CREATE TABLE IF NOT EXISTS generos (
    id   SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- 5. Usuário administrador padrão
-- Senha: admin123 (em produção, usar hash BCrypt)
INSERT INTO usuarios (login, senha, tipo)
VALUES ('admin', 'admin123', 'admin')
ON CONFLICT (login) DO NOTHING;

-- 6. Usuário comum padrão para testes
INSERT INTO usuarios (login, senha, tipo)
VALUES ('usuario', 'usuario123', 'comum')
ON CONFLICT (login) DO NOTHING;

-- 7. Alguns gêneros iniciais
INSERT INTO generos (nome) VALUES
    ('Aventura'),
    ('Estratégia'),
    ('RPG'),
    ('Ação'),
    ('Esportes'),
    ('Simulação'),
    ('Plataforma'),
    ('Terror')
ON CONFLICT (nome) DO NOTHING;
