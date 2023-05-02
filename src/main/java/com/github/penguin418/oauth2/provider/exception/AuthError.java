package com.github.penguin418.oauth2.provider.exception;

/**
 * RFC 6749
 */
public enum AuthError {
    INVALID_REQUEST("invalid_request", 400,"request is missing required parameter"),
    INVALID_REDIRECT_URI("invalid_request",400, "invalid redirection"),
    INVALID_REQUEST_RESPONSE_TYPE_IS_NOT_CODE("invalid_request",400, "invalid response type. value MUST be set to 'code'."),
    UNAUTHORIZED_CLIENT("unauthorized_client",401, "The client is not authorized to request an access token using this method."),
    ACCESS_DENIED("access_denied", 403,"The resource owner or authorization server denied the request."),
    ACCESS_DENIED_LOGIN_FAILURE("access_denied_login_failure", 401, "Incorrect username or password"),
    ACCESS_DENIED_VERIFIER_DOES_NOT_MATCH("verifier_does_not_match", 401, "code verifier does not match with code challenge"),

    UNSUPPORTED_RESPONSE_TYPE( "unsupported_response_type",400, "The authorization server does not support obtaining an access token using this method"),
    INVALID_SCOPE( "invalid_scope", 400,"The requested scope is invalid, unknown, or malformed."),
    SERVER_ERROR( "server_error", 500,"The authorization server encountered an unexpected event."),
    TEMPORARILY_UNAVAILABLE("temporarily_unavailable", 429, "The authorization server is currently busy.");


    private final String errorCode;
    private final Integer statusCode;
    private String errorDescription;
    private String state;

    private AuthError(String errorCode, int statusCode, String errorDescription) {
        this.errorCode = errorCode;
        this.statusCode=statusCode;
        this.errorDescription = errorDescription;
    }

    public AuthError withDetail(String detail){
        this.errorDescription += ", " + detail;
        return this;
    }

    public AuthError withState(String state){
        this.state = state;
        return this;
    }

    public AuthException exception(){
        return new AuthException(this);
    }

    public AuthException exception(Throwable t){
        return new AuthException(this, t);
    }
    public AuthException exceptionIfNotExpected(Throwable t){
        if (t instanceof AuthException){
            return (AuthException) t;
        }else
            return exception(t);

    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getState(){return this.state;}
}
