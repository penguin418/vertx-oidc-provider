package com.github.penguin418.oauth2.provider.dto;

import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.exception.AuthException;
import com.github.penguin418.oauth2.provider.validation.BasicABNFComparator;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Authorization Request<br>
 * [Code Grant 의 경우] RFC 6749 - 4.1.1
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthorizationRequest {
    public static final String SESSION_STORE_NAME = "authorization_request";
    private String responseType;
    private String clientId;
    private String redirectUri;
    private String scope;
    private String state;

    public static AuthorizationRequest fromQuery(HttpServerRequest request) throws AuthException {
        MultiMap queryParams = request.params();
        String responseType = queryParams.get("response_type");
        String clientId = queryParams.get("client_id");
        String redirectUri = queryParams.get("redirect_uri");
        String scope = queryParams.get("scope");
        String state = queryParams.get("state");
        return new AuthorizationRequest(responseType,clientId,redirectUri,scope,state);
    }
}
