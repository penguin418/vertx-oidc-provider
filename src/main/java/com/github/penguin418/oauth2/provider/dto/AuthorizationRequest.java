package com.github.penguin418.oauth2.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authorization Request<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.1
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorizationRequest {
    private String responseType;
    private String clientId;
    private String redirectUri;
    private String scope;
    private String state;
}
