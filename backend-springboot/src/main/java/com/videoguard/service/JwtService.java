package com.videoguard.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();

    private final ObjectMapper objectMapper;
    private final byte[] secretBytes;
    private final long expiresInSeconds;

    public JwtService(
            ObjectMapper objectMapper,
            @Value("${videoguard.jwt.secret:VideoGuardLocalDemoSecret}") String secret,
            @Value("${videoguard.jwt.expires-in-seconds:86400}") long expiresInSeconds) {
        this.objectMapper = objectMapper;
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.expiresInSeconds = expiresInSeconds;
    }

    public String generateToken(Long userId, String role) {
        try {
            Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("id", userId);
            payload.put("role", WorkflowConstants.normalizeRole(role));
            payload.put("exp", Instant.now().getEpochSecond() + expiresInSeconds);

            String encodedHeader = encodeJson(header);
            String encodedPayload = encodeJson(payload);
            String signingInput = encodedHeader + "." + encodedPayload;
            return signingInput + "." + sign(signingInput);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate token.", e);
        }
    }

    public Map<String, Object> verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token.");
            }
            String signingInput = parts[0] + "." + parts[1];
            if (!sign(signingInput).equals(parts[2])) {
                throw new IllegalArgumentException("Invalid token signature.");
            }
            Map<String, Object> payload = objectMapper.readValue(
                    BASE64_URL_DECODER.decode(parts[1]),
                    new TypeReference<>() {
                    });
            Number exp = (Number) payload.get("exp");
            if (exp == null || exp.longValue() < Instant.now().getEpochSecond()) {
                throw new IllegalArgumentException("Token expired.");
            }
            return payload;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token.", e);
        }
    }

    private String encodeJson(Map<String, Object> value) throws Exception {
        return BASE64_URL_ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
    }

    private String sign(String signingInput) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
        return BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
    }
}
