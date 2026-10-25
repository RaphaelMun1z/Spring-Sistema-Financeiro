ALTER TABLE tb_usuarios ADD COLUMN telegram_chat_id BIGINT;
ALTER TABLE tb_usuarios ADD COLUMN telegram_link_code VARCHAR(6);
ALTER TABLE tb_usuarios ADD COLUMN telegram_link_code_expires_at TIMESTAMP;
CREATE UNIQUE INDEX uk_tb_usuarios_telegram_chat_id ON tb_usuarios (telegram_chat_id);
CREATE UNIQUE INDEX uk_tb_usuarios_telegram_link_code ON tb_usuarios (telegram_link_code);
