package com.github.penguin418.oauth2.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Error Response<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.2.1
 * 아래 내용은 쿼리 파라미터로 제공됨
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    /**
     * error code<br>
     * [Code Grant 의 경우] 항상 제공, NQSCHAR 조건
     */
    private String error;
    /**
     * error_description<br>
     * [Code Grant 의 경우] 선택, NQSCHAR 조건
     */
    private String errorDescription;
    /**
     * error_uri<br>
     * [Code Grant 의 경우] 선택, NQCHAR 조건. client 가 사전에 등록한 uri만 허용
     */
    private String errorUri;
    /**
     * state<br>
     * [Code Grant 의 경우] 선택, 사용자가 state 를 제출한 경우
     */
    private String state;
}
