package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@DataObject
public class OAuth2User {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

    public OAuth2User(JsonObject jsonObject) {
        this.userId = jsonObject.getString("user_id");
        this.username = jsonObject.getString("username");
        this.password = jsonObject.getString("password");
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
