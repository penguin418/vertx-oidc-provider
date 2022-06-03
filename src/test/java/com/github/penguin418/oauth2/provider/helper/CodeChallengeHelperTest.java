package com.github.penguin418.oauth2.provider.helper;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class CodeChallengeHelperTest {
    CodeChallengeHelper codeChallengeHelper = new CodeChallengeHelper();

    @SneakyThrows
    @Test
    public void test1(){
        String codeChallenge = "CP8eXyaf/BWIzhq9mi82ImWZK5mcVo8cyuQGYTNR4JA=";
        String codeVerifier = "HfeAF-FEsf5-fe_22-fea12";

        Assertions.assertTrue(codeChallengeHelper.verifyCodeChallenge(codeChallenge, "S256", codeVerifier));
    }
}