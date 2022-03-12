package com.github.penguin418.oauth2.storage.memory.impl;

import com.github.penguin418.oauth2.provider.model.*;
import com.github.penguin418.oauth2.provider.service.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MemoryOAuth2StorageService implements OAuth2StorageService {
    private final AtomicInteger userId = new AtomicInteger(0);
    private final Map<String, Oauth2Client> oauth2ClientStorage = new HashMap<>();
    private final Map<String, OAuth2Permission> oAuth2PermissionStorage = new HashMap<>();
    private final Map<String, OAuth2User> oAuth2UserStorage = new HashMap<>();
    private final Map<String, OAuth2AccessToken> oAuth2AccessTokenStorage = new HashMap<>();
    private final Map<String, OAuth2Code> oAuth2CodeStorage = new HashMap<>();

    @Override
    public Future<Oauth2Client> putClient(Oauth2Client client) {
        Promise<Oauth2Client> promise = Promise.promise();
        oauth2ClientStorage.put(client.getClientId(), client);
        promise.complete(client);
        return promise.future();
    }

    @Override
    public Future<Oauth2Client> getClientByClientId(String userId) {
        Promise<Oauth2Client> promise = Promise.promise();
        Oauth2Client client = oauth2ClientStorage.get(userId);
        if (client == null)
            promise.fail("not found");
        else
            promise.complete(client);
        return promise.future();
    }

    @Override
    public Future<OAuth2Permission> putPermission(OAuth2Permission permission) {
        Promise<OAuth2Permission> promise = Promise.promise();
        final String permissionId = permission.getUserId() + "_" + permission.getClientId();
        oAuth2PermissionStorage.put(permissionId, permission);
        promise.complete(permission);
        return promise.future();

    }

    @Override
    public Future<OAuth2Permission> getPermissionByUserId(String userId, String clientId) {
        Promise<OAuth2Permission> promise = Promise.promise();
        final String permissionId = userId + "_" + clientId;
        promise.complete(oAuth2PermissionStorage.get(permissionId));
        return promise.future();
    }

    @Override
    public Future<OAuth2User> putUser(OAuth2User user) {
        Promise<OAuth2User> promise = Promise.promise();
        String id = "user_" + userId.getAndIncrement();
        oAuth2UserStorage.put(id, user);
        user.setUserId(id);
        promise.complete(user);
        return promise.future();
    }

    @Override
    public Future<OAuth2User> getUserByUserId(String userId) {
        Promise<OAuth2User> promise = Promise.promise();
        promise.complete(oAuth2UserStorage.get(userId));
        return promise.future();
    }

    @Override
    public Future<OAuth2AccessToken> putAccessToken(OAuth2AccessToken accessToken) {
        Promise<OAuth2AccessToken> promise = Promise.promise();
        oAuth2AccessTokenStorage.put(accessToken.getAccessToken(), accessToken);
        promise.complete(accessToken);
        return promise.future();
    }

    @Override
    public Future<OAuth2AccessToken> getAccessTokenDetail(String accessToken) {
        Promise<OAuth2AccessToken> promise = Promise.promise();
        promise.complete(oAuth2AccessTokenStorage.get(accessToken));
        return promise.future();
    }

    @Override
    public Future<OAuth2Code> putCode(OAuth2Code code) {
        Promise<OAuth2Code> promise = Promise.promise();
        oAuth2CodeStorage.put(code.getCode(), code);
        promise.complete(code);
        return promise.future();
    }

    @Override
    public Future<OAuth2Code> getCodeDetail(String code) {
        Promise<OAuth2Code> promise = Promise.promise();
        promise.complete(oAuth2CodeStorage.get(code));
        return promise.future();
    }

    @Override
    public Future<Void> deleteCode(String code) {
        Promise<Void> promise = Promise.promise();
        oAuth2CodeStorage.remove(code);
        promise.complete();
        return promise.future();
    }
}
