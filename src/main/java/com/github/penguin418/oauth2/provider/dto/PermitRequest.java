package com.github.penguin418.oauth2.provider.dto;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PermitRequest {
    public static final String SESSION_STORE_NAME = "permit_request";
    private String clientId;
    private String[] scopes;

    public PermitRequest(String clientId, String[] scopes) {
        this.clientId = clientId;
        this.scopes = scopes;
    }
}
