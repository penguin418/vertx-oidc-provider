package com.github.penguin418.oauth2.provider.model;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.Setter;

@DataObject
public class OAuth2User {
    @Getter
    @Setter
    @JsonProperty("user_id")
    private String userId;
    @Getter
    @Setter
    @JsonProperty("username")
    private String username;
    @Getter
    @Setter
    @JsonProperty("password")
    private String password;

    private boolean isNew = false;

    public OAuth2User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isNew = true;
    }

    public OAuth2User encrypt() {
        if (this.isNew) {
            this.password = BCrypt.withDefaults().hashToString(12, this.password.toCharArray());
            this.isNew = false;
        }
        return this;
    }

    public static OAuth2User getLoggedInUser(RoutingContext context) {
        final User userFromSession = context.user();
        if (userFromSession == null) return null;
        return new OAuth2User(userFromSession.principal());
    }

    public void addToSession(RoutingContext context) {
        context.setUser(User.create(toJson()));
    }

    public void removeFromSession(RoutingContext context) {
        context.clearUser();
    }

    public boolean verified(String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), this.password).verified;
    }

    public OAuth2User(JsonObject jsonObject) {
        this.userId = jsonObject.getString("user_id");
        this.username = jsonObject.getString("username");
        this.password = jsonObject.getString("password");
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}
