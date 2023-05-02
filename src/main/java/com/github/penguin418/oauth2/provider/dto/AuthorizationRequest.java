package com.github.penguin418.oauth2.provider.dto;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Authorization Request<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.1
 */
@NoArgsConstructor
@Getter
@Setter
public class AuthorizationRequest {
    public static final String SESSION_STORE_NAME = "authorization_request";
    private String responseType;
    private String grantType;
    private String clientId;
    private String redirectUri;
    private String scope;

    private String state;
    // PKCE(Proof Key for Code Exchange)
    private String code_challenge;
    private String code_challenge_method;

    public AuthorizationRequest(RoutingContext context){
    }

    public AuthorizationRequest(MultiMap map){
        this.responseType = map.get("response_type");
        this.grantType = map.get("grant_type");
        this.redirectUri = map.get("redirect_uri");
        this.clientId = map.get("client_id");
        this.scope = map.get("scope");
        this.state = map.get("state");
    }


    public boolean isAuthorizationCodeGrantRequest(){
        return "code".equals(this.responseType);
    }

    public boolean hasCodeChallenge(){ return null != this.code_challenge; }

    public boolean isImplicitGrantRequest(){
        return "token".equals(this.responseType);
    }

    public boolean isResourceOwnerPasswordCredentialsGrantRequest(){
        return "password".equals(this.grantType);
    }

    public boolean isClientCredentialsGrantRequest(){
        return "client_credentials".equals(this.grantType);
    }

}
