SET NAMES utf8mb4;

SET @has_violation_category = (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'video'
    AND column_name = 'violation_category'
);
SET @alter_video_sql = IF(
  @has_violation_category = 0,
  'ALTER TABLE video ADD COLUMN violation_category VARCHAR(32) NULL AFTER ai_risk_score',
  'DO 1'
);
PREPARE alter_video_stmt FROM @alter_video_sql;
EXECUTE alter_video_stmt;
DEALLOCATE PREPARE alter_video_stmt;

UPDATE `user`
SET role = CASE role
  WHEN 'USER' THEN '一般用户'
  WHEN 'REVIEWER' THEN '审核员'
  WHEN 'ADMIN' THEN '管理员'
  ELSE role
END;

UPDATE video
SET status = CASE status
  WHEN 'UPLOADED' THEN '已上传'
  WHEN 'PROCESSING' THEN '预审中'
  WHEN 'AI_PASSED' THEN '通过'
  WHEN 'AI_SUSPICIOUS' THEN '复审中'
  WHEN 'AI_VIOLATION' THEN '复审中'
  WHEN 'MANUAL_PASSED' THEN '通过'
  WHEN 'MANUAL_REJECTED' THEN '驳回'
  WHEN 'FAILED' THEN '待申诉'
  ELSE status
END;

UPDATE video
SET ai_risk_level = CASE ai_risk_level
  WHEN 'PASS' THEN '正常'
  WHEN 'SUSPICIOUS' THEN '可疑'
  WHEN 'VIOLATION' THEN '违规'
  ELSE ai_risk_level
END;

UPDATE video
SET final_result = CASE final_result
  WHEN 'PASS' THEN '正常'
  WHEN 'REJECT' THEN '违规'
  ELSE final_result
END;

UPDATE video
SET violation_category = '暴力'
WHERE violation_category IS NULL
  AND ai_risk_level IN ('可疑', '违规');

UPDATE sensitive_word
SET category = CASE category
  WHEN 'violence' THEN '暴力'
  WHEN 'porn' THEN '色情'
  WHEN 'illegal' THEN '政治敏感'
  WHEN 'custom' THEN '政治敏感'
  ELSE category
END;

UPDATE sensitive_hit
SET category = CASE category
  WHEN 'violence' THEN '暴力'
  WHEN 'porn' THEN '色情'
  WHEN 'illegal' THEN '政治敏感'
  WHEN 'custom' THEN '政治敏感'
  ELSE category
END;
