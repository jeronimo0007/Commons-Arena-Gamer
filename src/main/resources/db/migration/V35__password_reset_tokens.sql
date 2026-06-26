CREATE TABLE IF NOT EXISTS tblarena_password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_type VARCHAR(10) NOT NULL,
    user_id BIGINT NOT NULL,
    email VARCHAR(190) NOT NULL,
    token VARCHAR(128) NOT NULL,
    expires_at DATETIME NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY idx_arena_pwd_reset_token (token),
    INDEX idx_arena_pwd_reset_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
