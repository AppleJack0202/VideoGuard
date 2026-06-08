package com.videoguard.controller;

import com.videoguard.dto.SensitiveWordRequest;
import com.videoguard.dto.SensitiveWordResponse;
import com.videoguard.service.SensitiveWordService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensitive-words")
public class SensitiveWordController {

    private final SensitiveWordService sensitiveWordService;

    public SensitiveWordController(SensitiveWordService sensitiveWordService) {
        this.sensitiveWordService = sensitiveWordService;
    }

    @GetMapping
    public List<SensitiveWordResponse> list(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "enabled", required = false) Integer enabled) {
        return sensitiveWordService.list(category, enabled);
    }

    @PostMapping
    public SensitiveWordResponse create(@Valid @RequestBody SensitiveWordRequest request) {
        return sensitiveWordService.create(request);
    }

    @PutMapping("/{id}")
    public SensitiveWordResponse update(@PathVariable Long id, @Valid @RequestBody SensitiveWordRequest request) {
        return sensitiveWordService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sensitiveWordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
