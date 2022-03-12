package com.github.penguin418.oauth2.provider.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@DataObject
public class Oauth2Client {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("response_types")
    private List<String> responseTypes;
    @JsonProperty("scopes")
    private List<String> scopes;
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    public Oauth2Client(JsonObject jsonObject) {
        this.clientId = jsonObject.getString("client_id");
        this.clientSecret = jsonObject.getString("client_secret");
        this.responseTypes = jsonObject.getJsonArray("response_types").getList();
        this.scopes = jsonObject.getJsonArray("scopes").getList();
        this.redirectUris = jsonObject.getJsonArray("redirect_uris").getList();
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
