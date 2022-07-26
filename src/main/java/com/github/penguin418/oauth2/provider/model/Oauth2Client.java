package com.github.penguin418.oauth2.provider.model;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.penguin418.oauth2.provider.util.RandomGenerator;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@DataObject
public class Oauth2Client {
    public static final String SESSION_STORE_NAME = "authorization_request";

    @Getter @Setter
    @JsonProperty("client_id")
    private String clientId;
    @Getter @Setter
    @JsonProperty("client_secret")
    private String clientSecret;
    @Getter @Setter
    @JsonProperty("response_types")
    private List<String> responseTypes;
    @Getter @Setter
    @JsonProperty("scopes")
    private List<String> scopes;
    @Getter @Setter
    @JsonProperty("redirect_uris")
    private List<String> redirectUris;

    @JsonIgnore
    private boolean isNew = false;

    public Oauth2Client(List<String> responseTypes, List<String> scopes, List<String> redirectUris){
        this.responseTypes=responseTypes;
        this.scopes=scopes;
        this.redirectUris = redirectUris;
        this.isNew = true;
        generateClient();
    }

    public Oauth2Client encrypt(){
        if (this.isNew) {
            this.clientSecret = BCrypt.withDefaults().hashToString(12, this.clientSecret.toCharArray());
            this.isNew = false;
        }
        return this;
    }

    public boolean verified(String clientSecret){
        return BCrypt.verifyer().verify(clientSecret.toCharArray(), this.clientSecret).verified;
    }

    // @DataObject
    public Oauth2Client(JsonObject jsonObject){
        this.clientId=jsonObject.getString("client_id");
        this.clientSecret=jsonObject.getString("client_secret");
        this.responseTypes=jsonObject.getJsonArray("response_types").getList();
        this.scopes=jsonObject.getJsonArray("scopes").getList();
        this.redirectUris=jsonObject.getJsonArray("redirect_uris").getList();
    }

    /**
     *  AccessToken
     * RFC 6749 - A.12
     * VSCHAR
     * 1*( ALPHA / DIGIT / "-" / "." / "_" / "~" / "+" / "/" ) *"="
     */
    private void generateClient(){
        this.clientId = RandomGenerator.generateAccessToken(32);
        this.clientSecret = RandomGenerator.generateAccessToken(32);
    }
    // @DataObject
    public JsonObject toJson(){
        return JsonObject.mapFrom(this);
    }
}
