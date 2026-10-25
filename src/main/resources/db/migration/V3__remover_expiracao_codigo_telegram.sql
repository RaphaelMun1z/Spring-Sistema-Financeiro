ALTER TABLE tb_usuarios ALTER COLUMN telegram_link_code TYPE VARCHAR(64);
ALTER TABLE tb_usuarios DROP COLUMN telegram_link_code_expires_at;
