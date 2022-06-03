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
        final MultiMap map = context.queryParams().contains("response_type")
                ? context.request().params()
                : context.request().formAttributes();

        this.responseType = map.get("response_type");
        this.grantType = map.get("grant_type");
        this.redirectUri = map.get("redirect_uri");
        this.clientId = map.get("client_id");
        this.scope = map.get("scope");
        this.state = map.get("state");
    }


    public boolean isAuthorizationCodeGrantRequest(){
        return this.responseType.equals("code");
    }

    public boolean hasCodeChallenge(){ return this.code_challenge != null; }

    public boolean isImplicitGrantRequest(){
        return this.responseType.equals("token");
    }

    public boolean isResourceOwnerPasswordCredentialsGrantRequest(){
        return this.grantType.equals("password");
    }

    public boolean isClientCredentialsGrantRequest(){
        return this.grantType.equals("client_credentials");
    }

}
