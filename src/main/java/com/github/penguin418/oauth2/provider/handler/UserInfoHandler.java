package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.UserInfoResponse;
import com.github.penguin418.oauth2.provider.exception.AuthException;
import com.github.penguin418.oauth2.provider.helper.ClientAuthenticationHelper;
import com.github.penguin418.oauth2.provider.model.OAuth2AccessToken;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

@Slf4j
public class UserInfoHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final OAuth2StorageService storageService;
    private ClientAuthenticationHelper authenticationHelper = new ClientAuthenticationHelper();

    public UserInfoHandler(Vertx vertx) {
        this.vertx = vertx;
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }


    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.POST)) {
            handlePostRequest(event);
        } else event.fail(INVALID_REQUEST.exception());
    }

    private void handlePostRequest(RoutingContext event) {
        final String credential = authenticationHelper.parseResourceOwnerAuthenticationHeader(event);
        storageService.getAccessTokenDetail(credential)
                .compose(accessToken -> checkAccessToken(accessToken, credential))
                .compose(this::retrieveUserInfo)
                .compose(userInfo -> sendBackUserInfo(userInfo, event))
                .onFailure(fail -> {
                    event.fail(TEMPORARILY_UNAVAILABLE.exception());
                });
    }

    private Future<OAuth2AccessToken> checkAccessToken(OAuth2AccessToken accessToken, String credential){
        Promise<OAuth2AccessToken> promise = Promise.promise();
        Instant now = Instant.now();
        if (accessToken.getExpiresAt().isBefore(now)) {
            log.info("code expired");
            // 오래된 코드
            storageService.deleteAccessToken(credential);
            promise.fail(ACCESS_DENIED.exception());
        }else{
            promise.complete(accessToken);
        }
        return promise.future();
    }

    private Future<OAuth2User> retrieveUserInfo(OAuth2AccessToken accessToken){
        Promise<OAuth2User> promise = Promise.promise();
        storageService.getUserByUsername(accessToken.getUsername())
                .onSuccess(promise::complete)
                .onFailure(promise::fail);
        return promise.future();
    }

    private Future<Void> sendBackUserInfo(OAuth2User userInfo, RoutingContext event){
        Promise<Void> promise = Promise.promise();
        UserInfoResponse userInfoResponse = new UserInfoResponse(userInfo.getUserId());
        userInfoResponse.setPreferredUsername(userInfo.getUsername());
        userInfoResponse.setNickname(userInfo.getUsername());
        userInfoResponse.setEmail(userInfo.getUsername());
        event.response()
                .putHeader("Content-Type", "application/json")
                .send(Json.encode(userInfoResponse));
        promise.complete();
        return promise.future();
    }
}
