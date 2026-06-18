package com.videoguard.service;

import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public final class WorkflowConstants {

    public static final String STATUS_UPLOADED = "已上传";
    public static final String STATUS_PRE_REVIEWING = "预审中";
    public static final String STATUS_MANUAL_REVIEWING = "复审中";
    public static final String STATUS_APPEAL_PENDING = "待申诉";
    public static final String STATUS_PASSED = "通过";
    public static final String STATUS_REJECTED = "驳回";

    public static final String RISK_NORMAL = "正常";
    public static final String RISK_SUSPICIOUS = "可疑";
    public static final String RISK_VIOLATION = "违规";

    public static final String CATEGORY_VIOLENCE = "暴力";
    public static final String CATEGORY_PORN = "色情";
    public static final String CATEGORY_POLITICAL = "政治敏感";
    public static final String CATEGORY_OTHER = "其他违规";

    public static final String ROLE_USER = "一般用户";
    public static final String ROLE_REVIEWER = "审核员";
    public static final String ROLE_ADMIN = "管理员";

    public static final Set<String> REVIEWABLE_STATUSES = Set.of(STATUS_MANUAL_REVIEWING);
    public static final Set<String> REVIEW_SUBMIT_STATUSES = Set.of(STATUS_PASSED, STATUS_REJECTED, STATUS_APPEAL_PENDING);
    public static final Set<String> VIOLATION_CATEGORIES = Set.of(
            CATEGORY_VIOLENCE,
            CATEGORY_PORN,
            CATEGORY_POLITICAL,
            CATEGORY_OTHER);
    public static final Set<String> ROLES = Set.of(ROLE_USER, ROLE_REVIEWER, ROLE_ADMIN);

    private static final Map<String, String> LEGACY_RISK_MAP = Map.of(
            "PASS", RISK_NORMAL,
            "SUSPICIOUS", RISK_SUSPICIOUS,
            "VIOLATION", RISK_VIOLATION);

    private static final Map<String, String> LEGACY_CATEGORY_MAP = Map.of(
            "violence", CATEGORY_VIOLENCE,
            "porn", CATEGORY_PORN,
            "politics", CATEGORY_POLITICAL,
            "political", CATEGORY_POLITICAL,
            "illegal", CATEGORY_POLITICAL,
            "custom", CATEGORY_POLITICAL,
            "ad", CATEGORY_OTHER,
            "suspicious", CATEGORY_OTHER,
            "violation", CATEGORY_OTHER);

    private static final Map<String, String> LEGACY_ROLE_MAP = Map.of(
            "USER", ROLE_USER,
            "REVIEWER", ROLE_REVIEWER,
            "ADMIN", ROLE_ADMIN);

    private WorkflowConstants() {
    }

    public static String normalizeRiskLevel(String riskLevel) {
        if (riskLevel == null) {
            return null;
        }
        return LEGACY_RISK_MAP.getOrDefault(riskLevel.trim().toUpperCase(), riskLevel.trim());
    }

    public static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }
        String trimmed = category.trim();
        String lower = trimmed.toLowerCase();
        if (Set.of("normal", "pass", "none", "-", "正常").contains(lower)) {
            return null;
        }
        return LEGACY_CATEGORY_MAP.getOrDefault(lower, trimmed);
    }

    public static String normalizeCategories(String categories) {
        if (categories == null || categories.isBlank()) {
            return null;
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String item : categories.split("[,，;/；、\\s]+")) {
            String category = normalizeCategory(item);
            if (category != null && !category.isBlank()) {
                normalized.add(category);
            }
        }
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized.stream().collect(Collectors.joining(","));
    }

    public static String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return ROLE_USER;
        }
        String trimmed = role.trim();
        return LEGACY_ROLE_MAP.getOrDefault(trimmed.toUpperCase(), trimmed);
    }
}
