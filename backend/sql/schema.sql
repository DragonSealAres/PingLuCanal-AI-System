CREATE DATABASE IF NOT EXISTS pinglu_ai
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE pinglu_ai;

CREATE TABLE IF NOT EXISTS hazard_report (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  report_no VARCHAR(50) NOT NULL COMMENT 'Hazard report number',
  report_user VARCHAR(100) NOT NULL COMMENT 'Reporter name',
  location VARCHAR(255) NOT NULL COMMENT 'Hazard location',
  longitude DECIMAL(10,6) NOT NULL COMMENT 'Longitude',
  latitude DECIMAL(10,6) NOT NULL COMMENT 'Latitude',
  image_url VARCHAR(500) COMMENT 'Hazard image URL',
  hazard_type VARCHAR(50) NOT NULL COMMENT 'Hazard type',
  risk_level VARCHAR(20) NOT NULL COMMENT 'Risk level',
  description VARCHAR(1000) NOT NULL COMMENT 'Hazard description',
  ai_result TEXT COMMENT 'AI analysis result',
  status VARCHAR(30) NOT NULL DEFAULT '待审核' COMMENT 'Current status',
  create_time DATETIME NOT NULL COMMENT 'Create time',
  update_time DATETIME NOT NULL COMMENT 'Update time',
  UNIQUE KEY uk_hazard_report_report_no (report_no),
  KEY idx_hazard_report_status (status),
  KEY idx_hazard_report_create_time (create_time)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Hazard report table';

CREATE TABLE IF NOT EXISTS work_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(50) NOT NULL COMMENT 'Work order number',
  hazard_id BIGINT NOT NULL COMMENT 'Related hazard_report.id',
  title VARCHAR(200) NOT NULL COMMENT 'Work order title',
  handler VARCHAR(100) NOT NULL COMMENT 'Handler name',
  requirement VARCHAR(1000) COMMENT 'Handle requirement',
  status VARCHAR(30) NOT NULL COMMENT 'Work order status',
  handle_remark VARCHAR(1000) COMMENT 'Handle remark',
  handle_image VARCHAR(500) COMMENT 'Handle completion image',
  create_time DATETIME NOT NULL COMMENT 'Create time',
  update_time DATETIME NOT NULL COMMENT 'Update time',
  UNIQUE KEY uk_work_order_order_no (order_no),
  KEY idx_work_order_hazard_id (hazard_id),
  KEY idx_work_order_status (status),
  KEY idx_work_order_create_time (create_time)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='Work order table';
