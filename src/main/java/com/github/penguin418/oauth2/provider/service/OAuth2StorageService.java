package com.github.penguin418.oauth2.provider.service;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.model.*;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ServiceProxyBuilder;

@ProxyGen
public interface OAuth2StorageService {
    static final String DEFAULT_STORAGE_SERVICE_ADDRESS = "oauth2.storage.service.default";

    static OAuth2StorageService createProxy(Vertx vertx) {
        return new ServiceProxyBuilder(vertx).setAddress(DEFAULT_STORAGE_SERVICE_ADDRESS).build(OAuth2StorageService.class);
    }

    Future<Oauth2Client> putClient(Oauth2Client client);

    Future<Oauth2Client> getClientByClientId(String clientId);

    Future<OAuth2Permission> putPermission(OAuth2Permission permission);

    Future<OAuth2Permission> getPermissionByUserId(String userId, String clientId);

    Future<OAuth2User> putUser(OAuth2User user);

    Future<OAuth2User> getUserByUserId(String userId);

    Future<OAuth2User> getUserByUsername(String username);

    Future<OAuth2AccessToken> putAccessToken(OAuth2AccessToken accessToken);

    Future<OAuth2AccessToken> getAccessTokenDetail(String accessToken);

    Future<OAuth2AccessToken> getAccessTokenDetailByRefreshToken(String refreshToken);

    Future<Void> deleteAccessToken(String accessToken);

    Future<OAuth2Code> putCode(OAuth2Code code);

    Future<OAuth2Code> getCodeDetail(String code);

    Future<Void> deleteCode(String code);
}
