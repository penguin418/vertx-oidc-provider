package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@DataObject
public class OAuth2AccessToken {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("authentication")
    private String authentication;
    @JsonProperty("expires_at")
    private Instant expiresAt;

    public OAuth2AccessToken(JsonObject jsonObject) {
        this.clientId = jsonObject.getString("client_id");
        this.username = jsonObject.getString("username");
        this.accessToken = jsonObject.getString("access_token");
        this.refreshToken = jsonObject.getString("refresh_token");
        this.authentication = jsonObject.getString("authentication");
        this.expiresAt = jsonObject.getInstant("expires_at");
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
