package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.penguin418.oauth2.provider.util.RandomGenerator;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
    @JsonProperty("expires_at")
    private Instant expiresAt;

    public OAuth2AccessToken(String clientId, String username){
        this.clientId = clientId;
        this.username = username;
        this.expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
        generateAccessToken();
    }

    public OAuth2AccessToken(JsonObject jsonObject) {
        this.clientId = jsonObject.getString("client_id");
        this.username = jsonObject.getString("username");
        this.accessToken = jsonObject.getString("access_token");
        this.refreshToken = jsonObject.getString("refresh_token");
        this.expiresAt = jsonObject.getInstant("expires_at");
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
    /**
     *  AccessToken
     * RFC 6749 - A.12
     * VSCHAR
     * 1*( ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/" ) *"="
     */
    private void generateAccessToken(){
        this.accessToken = RandomGenerator.generateAccessToken(32);
        this.refreshToken = RandomGenerator.generateAccessToken(32);
    }
}
