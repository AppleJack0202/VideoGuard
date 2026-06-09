CREATE TABLE IF NOT EXISTS `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL UNIQUE,
  display_name VARCHAR(64) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL DEFAULT '一般用户',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS video (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  uploader_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  original_filename VARCHAR(255) NOT NULL,
  stored_filename VARCHAR(255) NOT NULL,
  file_path VARCHAR(512) NOT NULL,
  file_size BIGINT,
  duration DOUBLE,
  width INT,
  height INT,
  fps DOUBLE,
  status VARCHAR(32) NOT NULL DEFAULT '已上传',
  ai_risk_level VARCHAR(32),
  ai_risk_score DOUBLE DEFAULT 0,
  violation_category VARCHAR(32),
  final_result VARCHAR(32),
  final_comment TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_video_status (status),
  INDEX idx_video_ai_risk_level (ai_risk_level),
  INDEX idx_video_violation_category (violation_category),
  INDEX idx_video_uploader_id (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS video_frame (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  video_id BIGINT NOT NULL,
  frame_path VARCHAR(512) NOT NULL,
  timestamp_sec DOUBLE NOT NULL,
  label VARCHAR(64),
  confidence DOUBLE DEFAULT 0,
  risk_score DOUBLE DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_video_frame_video_id (video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sensitive_word (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  word VARCHAR(128) NOT NULL,
  category VARCHAR(64) NOT NULL,
  weight INT NOT NULL DEFAULT 10,
  enabled TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_sensitive_word_enabled (enabled),
  INDEX idx_sensitive_word_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sensitive_hit (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  video_id BIGINT NOT NULL,
  source_type VARCHAR(32) NOT NULL,
  word VARCHAR(128) NOT NULL,
  category VARCHAR(64) NOT NULL,
  weight INT NOT NULL,
  context_text TEXT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_sensitive_hit_video_id (video_id),
  INDEX idx_sensitive_hit_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS ai_review_result (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  video_id BIGINT NOT NULL,
  text_score DOUBLE DEFAULT 0,
  image_score DOUBLE DEFAULT 0,
  asr_score DOUBLE DEFAULT 0,
  final_score DOUBLE DEFAULT 0,
  risk_level VARCHAR(32) NOT NULL,
  asr_text MEDIUMTEXT,
  raw_result_json JSON,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ai_review_result_video_id (video_id),
  INDEX idx_ai_review_result_risk_level (risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS review_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  video_id BIGINT NOT NULL,
  reviewer_id BIGINT NOT NULL,
  before_status VARCHAR(32),
  after_status VARCHAR(32),
  before_result VARCHAR(32),
  after_result VARCHAR(32),
  comment TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_review_log_video_id (video_id),
  INDEX idx_review_log_reviewer_id (reviewer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
