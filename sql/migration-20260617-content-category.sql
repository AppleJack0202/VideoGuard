SET @has_content_category = (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND column_name = 'content_category'
);
SET @ddl = IF(
  @has_content_category = 0,
  'ALTER TABLE video ADD COLUMN content_category VARCHAR(64) NULL AFTER violation_category',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_category_confidence = (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND column_name = 'category_confidence'
);
SET @ddl = IF(
  @has_category_confidence = 0,
  'ALTER TABLE video ADD COLUMN category_confidence DOUBLE NULL AFTER content_category',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_category_reason = (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND column_name = 'category_reason'
);
SET @ddl = IF(
  @has_category_reason = 0,
  'ALTER TABLE video ADD COLUMN category_reason TEXT NULL AFTER category_confidence',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_review_strategy = (
  SELECT COUNT(*) FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND column_name = 'review_strategy'
);
SET @ddl = IF(
  @has_review_strategy = 0,
  'ALTER TABLE video ADD COLUMN review_strategy VARCHAR(64) NULL AFTER category_reason',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_idx_video_content_category = (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND index_name = 'idx_video_content_category'
);
SET @ddl = IF(
  @has_idx_video_content_category = 0,
  'CREATE INDEX idx_video_content_category ON video (content_category)',
  'SELECT 1'
);
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
