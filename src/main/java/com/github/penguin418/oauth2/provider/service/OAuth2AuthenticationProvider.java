package com.github.penguin418.oauth2.provider.service;

import com.github.penguin418.oauth2.provider.model.OAuth2User;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.auth.authorization.Authorization;
import io.vertx.ext.auth.authorization.RoleBasedAuthorization;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Authorization 헤더로 부터 clientId와 clientSecret 을 확인
 */
@Slf4j
public class OAuth2AuthenticationProvider implements AuthenticationProvider {
    private final OAuth2StorageService storageService;

    public OAuth2AuthenticationProvider(Vertx vertx){
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }

    @Override
    public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> resultHandler) {
        log.info("[auth - provider] cred={}", credentials.encode());
        final String username = credentials.getString("username");
        final String password = credentials.getString("password");

        storageService.getUserByUsername(username).onSuccess(user->{
            if (user.verified(password)){
                log.info("verified !!");
                resultHandler.handle(createUser(user));
            }else{
                log.info("not - verified !!");
                resultHandler.handle(null);
            }
        }).onFailure(fail->{
            log.error("error : {}", fail.toString());
            resultHandler.handle(null);
        });
    }

    private Future<User> createUser(OAuth2User subject){
        Promise<User> promise = Promise.promise();
        User user = User.create(subject.toJson());

        Set<Authorization> result = new HashSet<>();
        String principal = user.principal().toString();
        result.add(RoleBasedAuthorization.create(principal));
        user.authorizations().add("authentication", result);

        promise.complete(user);
        return promise.future();
    }
}
