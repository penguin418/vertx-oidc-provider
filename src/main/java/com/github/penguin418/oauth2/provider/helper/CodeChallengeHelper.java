package com.github.penguin418.oauth2.provider.helper;

import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class CodeChallengeHelper {
    public Boolean verifyCodeChallenge(String codeChallenge, String codeChallengeMethod, String codeVerifier) {
        if ("S256".equalsIgnoreCase(codeChallengeMethod)) {
            String encrypted = encryptSHA256(codeVerifier);
            return codeChallenge.equals(encrypted);
        } else {
            return codeVerifier.equals(codeChallenge);
        }
    }

    @SneakyThrows
    private String encryptSHA256(String value) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encoded = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encoded);
    }
}
