package com.github.penguin418.oauth2.provider.exception;

public class AuthException extends RuntimeException {
    private final AuthError authError;

    public AuthException(AuthError authError) {
        super(authError.getErrorCode());
        this.authError = authError;
    }

    public AuthError getErrorMsg() {
        return this.authError;
    }
}
