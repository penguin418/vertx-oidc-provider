package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@DataObject
public class OAuth2Permission {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("scopes")
    private List<String> scopes;

    public OAuth2Permission(JsonObject jsonObject) {
        this.userId = jsonObject.getString("user_id");
        this.clientId = jsonObject.getString("client_id");
        this.scopes = jsonObject.getJsonArray("scopes").getList();
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
