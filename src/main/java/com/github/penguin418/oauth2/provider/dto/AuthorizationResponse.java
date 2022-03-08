package com.github.penguin418.oauth2.provider.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authorization Response<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.2
 * 아래 내용은 쿼리 파라미터로 추가되어 반환됨
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorizationResponse {
    /**
     * code<br>
     * [Code Grant 의 경우]
     * 필수, auth 서버에서 생성됨
     * Code 의 유효기간은 10분이 추천되며, 1회만 사용가능해야 함.
     */
    private String code;
    /**
     * state<br>
     * [Code Grant 의 경우] 사용자가 제공할 경우 동일 값 필수
     */
    private String state;
}
