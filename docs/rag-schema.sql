CREATE TABLE IF NOT EXISTS knowledge_document (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  document_name VARCHAR(200) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_type VARCHAR(30) NOT NULL,
  status VARCHAR(30) NOT NULL,
  chunk_count INT NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  INDEX idx_knowledge_document_status (status),
  INDEX idx_knowledge_document_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS knowledge_chunk (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  document_id BIGINT NOT NULL,
  chunk_index INT NOT NULL,
  content TEXT NOT NULL,
  embedding LONGTEXT NOT NULL,
  source_name VARCHAR(200) NOT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_knowledge_chunk_document_id (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chat_session (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(80) NOT NULL UNIQUE,
  user_name VARCHAR(100) NOT NULL,
  title VARCHAR(200) NOT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  INDEX idx_chat_session_update_time (update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  session_id VARCHAR(80) NOT NULL,
  role VARCHAR(20) NOT NULL,
  content TEXT NOT NULL,
  sources TEXT NULL,
  create_time DATETIME NOT NULL,
  INDEX idx_chat_message_session_time (session_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
