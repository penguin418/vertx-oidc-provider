package com.github.penguin418.oauth2.provider.service;

import com.github.penguin418.oauth2.provider.model.OAuth2User;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final OAuth2StorageService storageService;

    public OAuth2AuthenticationProvider(Vertx vertx){
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        log.info("credentials={}", credentials.encode());
        final String username = credentials.getString("username");
        final String password = credentials.getString("password");

        storageService.getUserByUsername(username).onSuccess(user->{
            if (user.verified(password)){
                resultHandler.handle(toUser(user));
            }
        });
    }

    private Future<User> toUser(OAuth2User user){
        Promise<User> promise = Promise.promise();
        promise.complete(User.create(user.toJson()));
        return promise.future();
    }
}
