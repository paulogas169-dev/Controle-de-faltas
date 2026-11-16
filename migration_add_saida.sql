-- migration_add_saida.sql
-- Adiciona coluna para registrar horário de saída
ALTER TABLE frequencia ADD COLUMN saida_hora DATETIME NULL;