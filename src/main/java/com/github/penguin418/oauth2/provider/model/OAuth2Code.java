package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Getter
@Setter
@DataObject
public class OAuth2Code {
    @JsonProperty("code")
    private String code;
    @JsonProperty("authentication")
    private String authentication;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    @JsonProperty("code_challenge")
    private String codeChallenge;
    @JsonProperty("code_challenge_method")
    private String codeChallengeMethod;
    @JsonProperty("expires_at")
    private Instant expiresAt;

    public OAuth2Code(String authentication, String redirectUri) {
        this.code = UUID.randomUUID().toString();
        this.authentication = authentication;
        this.redirectUri = redirectUri;
        this.expiresAt = Instant.now().plus(1, ChronoUnit.MINUTES);
    }

    public OAuth2Code(JsonObject jsonObject) {
        this.code = jsonObject.getString("code");
        this.authentication = jsonObject.getString("authentication");
        this.redirectUri = jsonObject.getString("redirect_uri");
        this.codeChallenge = jsonObject.getString("code_challenge");
        this.codeChallengeMethod = jsonObject.getString("code_challenge_method");
        this.expiresAt = jsonObject.getInstant("expires_at");
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
