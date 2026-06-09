SET NAMES utf8mb4;

INSERT IGNORE INTO `user` (username, display_name, password_hash, role)
VALUES
  ('admin', '系统管理员', 'CHANGE_ME_HASH', '管理员'),
  ('reviewer', '审核员一号', 'CHANGE_ME_HASH', '审核员'),
  ('user', '普通用户一号', 'CHANGE_ME_HASH', '一般用户');

INSERT INTO sensitive_word (word, category, weight, enabled)
VALUES
  ('测试违规', '暴力', 40, 1),
  ('暴力', '暴力', 30, 1),
  ('危险', '暴力', 20, 1),
  ('低俗', '色情', 30, 1),
  ('赌博', '政治敏感', 40, 1),
  ('诈骗', '政治敏感', 40, 1);
