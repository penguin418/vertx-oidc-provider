package com.github.penguin418.oauth2.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccessTokenResponse {
    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String refreshToken;
}
