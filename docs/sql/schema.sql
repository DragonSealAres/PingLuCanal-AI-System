-- 平陆运河AI水上安全巡检系统完整建表SQL
-- 执行方式：在项目根目录运行 mysql -u root -p < docs/sql/schema.sql

CREATE DATABASE IF NOT EXISTS pinglu_ai
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE pinglu_ai;

CREATE TABLE IF NOT EXISTS hazard_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_no VARCHAR(50) NOT NULL COMMENT '隐患编号',
  report_user VARCHAR(100) NOT NULL COMMENT '上报人',
  location VARCHAR(255) NOT NULL COMMENT '隐患位置',
  longitude DECIMAL(10,6) NOT NULL COMMENT '经度',
  latitude DECIMAL(10,6) NOT NULL COMMENT '纬度',
  image_url VARCHAR(500) COMMENT '隐患图片地址',
  hazard_type VARCHAR(50) NOT NULL COMMENT '隐患类型',
  risk_level VARCHAR(20) NOT NULL COMMENT '风险等级',
  description VARCHAR(1000) NOT NULL COMMENT '隐患描述',
  ai_result TEXT COMMENT 'AI识别结果JSON',
  status VARCHAR(30) NOT NULL DEFAULT '待审核' COMMENT '隐患状态',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY uk_hazard_report_report_no (report_no),
  KEY idx_hazard_report_status (status),
  KEY idx_hazard_report_create_time (create_time),
  KEY idx_hazard_report_location (longitude, latitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='隐患上报表';

CREATE TABLE IF NOT EXISTS work_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL COMMENT '工单编号',
  hazard_id BIGINT NOT NULL COMMENT '关联hazard_report.id',
  title VARCHAR(200) NOT NULL COMMENT '工单标题',
  handler VARCHAR(100) NOT NULL COMMENT '处置人员',
  requirement VARCHAR(1000) COMMENT '处置要求',
  status VARCHAR(30) NOT NULL COMMENT '工单状态',
  handle_remark VARCHAR(1000) COMMENT '处置说明',
  handle_image VARCHAR(500) COMMENT '处置后图片',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY uk_work_order_order_no (order_no),
  KEY idx_work_order_hazard_id (hazard_id),
  KEY idx_work_order_status (status),
  KEY idx_work_order_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单表';

CREATE TABLE IF NOT EXISTS knowledge_document (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  document_name VARCHAR(200) NOT NULL COMMENT '知识文档名称',
  file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
  file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
  file_type VARCHAR(30) NOT NULL COMMENT '文件类型',
  status VARCHAR(30) NOT NULL COMMENT '解析状态',
  chunk_count INT NOT NULL DEFAULT 0 COMMENT '切片数量',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  KEY idx_knowledge_document_status (status),
  KEY idx_knowledge_document_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库文档表';

CREATE TABLE IF NOT EXISTS knowledge_chunk (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  document_id BIGINT NOT NULL COMMENT '关联knowledge_document.id',
  chunk_index INT NOT NULL COMMENT '切片序号',
  content TEXT NOT NULL COMMENT '切片内容',
  embedding LONGTEXT NOT NULL COMMENT '向量JSON',
  source_name VARCHAR(200) NOT NULL COMMENT '来源名称',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  KEY idx_knowledge_chunk_document_id (document_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库切片表';

CREATE TABLE IF NOT EXISTS chat_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(80) NOT NULL COMMENT '会话ID',
  user_name VARCHAR(100) NOT NULL COMMENT '用户名称',
  title VARCHAR(200) NOT NULL COMMENT '会话标题',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  update_time DATETIME NOT NULL COMMENT '更新时间',
  UNIQUE KEY uk_chat_session_session_id (session_id),
  KEY idx_chat_session_update_time (update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG会话表';

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(80) NOT NULL COMMENT '会话ID',
  role VARCHAR(20) NOT NULL COMMENT '消息角色',
  content TEXT NOT NULL COMMENT '消息内容',
  sources TEXT NULL COMMENT '来源引用JSON',
  create_time DATETIME NOT NULL COMMENT '创建时间',
  KEY idx_chat_message_session_time (session_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='RAG消息表';
