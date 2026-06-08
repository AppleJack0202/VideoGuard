package com.videoguard.repository;

import com.videoguard.entity.SensitiveWord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long>, JpaSpecificationExecutor<SensitiveWord> {

    List<SensitiveWord> findByEnabled(Integer enabled);
}
