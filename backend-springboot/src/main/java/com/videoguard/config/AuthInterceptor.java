package com.videoguard.config;

import com.videoguard.service.JwtService;
import com.videoguard.service.WorkflowConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public AuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (HttpMethod.OPTIONS.matches(request.getMethod()) || isPublicPath(request.getRequestURI())) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token.");
            return false;
        }

        Map<String, Object> payload;
        try {
            payload = jwtService.verify(header.substring("Bearer ".length()));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return false;
        }

        Long userId = ((Number) payload.get("id")).longValue();
        String role = WorkflowConstants.normalizeRole((String) payload.get("role"));
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentUserRole", role);

        if (!isAllowed(request, role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission denied.");
            return false;
        }
        return true;
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/health")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.startsWith("/uploads/");
    }

    private boolean isAllowed(HttpServletRequest request, String role) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (WorkflowConstants.ROLE_ADMIN.equals(role)) {
            return true;
        }
        if (path.equals("/api/auth/me")) {
            return true;
        }
        if (WorkflowConstants.ROLE_REVIEWER.equals(role)) {
            return path.startsWith("/api/review/")
                    || (HttpMethod.GET.matches(method) && path.startsWith("/api/videos/"))
                    || (HttpMethod.POST.matches(method) && path.matches("/api/videos/\\d+/analyze"))
                    || (HttpMethod.POST.matches(method) && path.matches("/api/videos/\\d+/asr/refresh"))
                    || (HttpMethod.GET.matches(method) && path.equals("/api/sensitive-words"))
                    || (HttpMethod.POST.matches(method) && path.equals("/api/sensitive-words"));
        }
        if (WorkflowConstants.ROLE_USER.equals(role)) {
            return (HttpMethod.POST.matches(method) && path.equals("/api/videos/upload"))
                    || (HttpMethod.GET.matches(method) && path.equals("/api/videos"))
                    || (HttpMethod.GET.matches(method) && path.matches("/api/videos/\\d+"))
                    || (HttpMethod.GET.matches(method) && path.matches("/api/videos/\\d+/play"));
        }
        return false;
    }
}
