package com.github.penguin418.oauth2.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authorization Request<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.3
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccessTokenRequest {
    /**
     * grant_type<br>
     * [Code Grant 의 경우] 필수. 그 값은 `authorization_code`여야 함.
     */
    private String grantType;
    /**
     * code<br>
     * [Code Grant 의 경우] 필수. 그 값은 authorization server에서 받은 값이어야 함.
     */
    private String code;
    /**
     * redirect_uri<br>
     * [Code Grant 의 경우] Authorization Request에서 사용된 경우 필수. 그 값은 Authorization Request에 사용된 것과 동일해야 함.
     */
    private String redirectUri;
    /**
     * client_id<br>
     * [Code Grant 의 경우] 필수
     */
    private String clientId;
    /**
     *  code_verifier<br>
     *  PKCE(Proof Key for Code Exchange) 에서 사용되는 원본 code_challenge 의 원본
     */
    private String code_verifier;
}
