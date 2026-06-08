SET NAMES utf8mb4;

INSERT IGNORE INTO `user` (username, password_hash, role)
VALUES
  ('admin', 'CHANGE_ME_HASH', 'ADMIN'),
  ('reviewer', 'CHANGE_ME_HASH', 'REVIEWER'),
  ('user', 'CHANGE_ME_HASH', 'USER');

INSERT INTO sensitive_word (word, category, weight, enabled)
VALUES
  ('测试违规', 'violence', 40, 1),
  ('暴力', 'violence', 30, 1),
  ('危险', 'violence', 20, 1),
  ('低俗', 'porn', 30, 1),
  ('赌博', 'illegal', 40, 1),
  ('诈骗', 'illegal', 40, 1);
