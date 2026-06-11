package com.videoguard.service;

import com.videoguard.dto.SensitiveWordRequest;
import com.videoguard.dto.SensitiveWordResponse;
import com.videoguard.entity.SensitiveWord;
import com.videoguard.repository.SensitiveWordRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SensitiveWordService {

    private final SensitiveWordRepository sensitiveWordRepository;

    public SensitiveWordService(SensitiveWordRepository sensitiveWordRepository) {
        this.sensitiveWordRepository = sensitiveWordRepository;
    }

    @Transactional(readOnly = true)
    public List<SensitiveWordResponse> list(String category, Integer enabled) {
        Specification<SensitiveWord> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if (enabled != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), normalizeEnabled(enabled)));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return sensitiveWordRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(SensitiveWordResponse::from)
                .toList();
    }

    @Transactional
    public SensitiveWordResponse create(SensitiveWordRequest request) {
        SensitiveWord sensitiveWord = new SensitiveWord();
        applyRequest(sensitiveWord, request);
        return SensitiveWordResponse.from(sensitiveWordRepository.save(sensitiveWord));
    }

    @Transactional
    public SensitiveWordResponse createSuggestion(SensitiveWordRequest request) {
        SensitiveWord sensitiveWord = new SensitiveWord();
        applyRequest(sensitiveWord, request);
        sensitiveWord.setEnabled(0);
        return SensitiveWordResponse.from(sensitiveWordRepository.save(sensitiveWord));
    }

    @Transactional
    public SensitiveWordResponse update(Long id, SensitiveWordRequest request) {
        SensitiveWord sensitiveWord = sensitiveWordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sensitive word not found: " + id));
        applyRequest(sensitiveWord, request);
        return SensitiveWordResponse.from(sensitiveWordRepository.save(sensitiveWord));
    }

    @Transactional
    public void delete(Long id) {
        if (!sensitiveWordRepository.existsById(id)) {
            throw new IllegalArgumentException("Sensitive word not found: " + id);
        }
        sensitiveWordRepository.deleteById(id);
    }

    private void applyRequest(SensitiveWord sensitiveWord, SensitiveWordRequest request) {
        String word = request.getWord() == null ? "" : request.getWord().trim();
        String category = request.getCategory() == null ? "" : request.getCategory().trim();
        if (!StringUtils.hasText(word)) {
            throw new IllegalArgumentException("Sensitive word is required.");
        }
        if (!StringUtils.hasText(category)) {
            throw new IllegalArgumentException("Category is required.");
        }

        sensitiveWord.setWord(word);
        sensitiveWord.setCategory(category);
        sensitiveWord.setWeight(request.getWeight());
        sensitiveWord.setEnabled(normalizeEnabled(request.getEnabled()));
    }

    private Integer normalizeEnabled(Integer enabled) {
        return enabled != null && enabled == 1 ? 1 : 0;
    }
}
