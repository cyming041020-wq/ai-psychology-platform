-- Psychology platform initial schema.
-- The script is intentionally safe to run more than once.

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'USER',
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE IF NOT EXISTS emotion_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    emotion VARCHAR(32) NOT NULL,
    intensity TINYINT NOT NULL,
    note VARCHAR(1000),
    recorded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_emotion_record_user
        FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_emotion_record_user_time (user_id, recorded_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS consultation_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_type VARCHAR(32) NOT NULL DEFAULT 'AI',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    started_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at DATETIME,
    CONSTRAINT fk_consultation_session_user
        FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_consultation_session_user_status (user_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS consultation_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    role VARCHAR(16) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultation_message_session
        FOREIGN KEY (session_id) REFERENCES consultation_session(id),
    INDEX idx_consultation_message_session_time (session_id, created_at, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crisis_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    risk_level VARCHAR(16) NOT NULL,
    trigger_source VARCHAR(32) NOT NULL,
    description VARCHAR(2000),
    handled TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    handled_at DATETIME,
    CONSTRAINT fk_crisis_event_user
        FOREIGN KEY (user_id) REFERENCES sys_user(id),
    INDEX idx_crisis_event_risk_handled (risk_level, handled, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
