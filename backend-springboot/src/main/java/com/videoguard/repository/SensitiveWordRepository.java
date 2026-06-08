package com.videoguard.repository;

import com.videoguard.entity.SensitiveWord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {

    List<SensitiveWord> findByEnabled(Integer enabled);
}

