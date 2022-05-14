package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.helper.ClientAuthenticationHelper;
import com.github.penguin418.oauth2.provider.model.OAuth2AccessToken;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static com.github.penguin418.oauth2.provider.exception.AuthError.ACCESS_DENIED;
import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

@Slf4j
public class TokenHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final OAuth2StorageService storageService;
    private static final ClientAuthenticationHelper clientAuthHelper = new ClientAuthenticationHelper();

    public TokenHandler(Vertx vertx) {
        this.vertx = vertx;
        storageService = OAuth2StorageService.createProxy(vertx);
    }

    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.POST)) {
            handlePostRequest(event);
        } else {
            event.fail(INVALID_REQUEST.exception());
        }
    }


    private void handlePostRequest(RoutingContext event) {
        final String grantType = event.request().getFormAttribute("grant_type");
        if (grantType.equals("authorization_code")) {
            handlePostAuthorizationCodeRequest(event);
        } else {
            // 현재는 지원하지 않는 기능
            event.fail(INVALID_REQUEST.exception());
        }
    }

    private void handlePostAuthorizationCodeRequest(RoutingContext event) {
        final String code = event.request().getFormAttribute("code");
        final String redirectUri = event.request().getFormAttribute("redirect_uri");
        final String clientId = event.request().getFormAttribute("client_id");
        final String authorization = event.request().getHeader("Authorization");

        storageService.getClientByClientId(clientId)
                .compose(clientDetail -> clientAuthHelper.tryAuthenticate(event, clientDetail))
                .compose(onVerified -> storageService.getCodeDetail(code))
                .compose(codeDetail -> sendBackAccessToken(event, code, redirectUri, clientId, codeDetail))
                .onFailure(event::fail);
    }

    private Future<OAuth2AccessToken> sendBackAccessToken(RoutingContext event, String code, String redirectUri, String clientId, OAuth2Code codeDetail) {
        if (codeDetail == null) throw ACCESS_DENIED.exception();
        Instant now = Instant.now();
        if (codeDetail.getExpiresAt().isBefore(now)) {
            log.info("code expired");
            // 오래된 코드
            storageService.deleteCode(code);
            throw ACCESS_DENIED.exception();
        }
        if (!codeDetail.getRedirectUri().equals(redirectUri)) { // 리다이렉션 url이 다름
            log.info("different redirect_uri");
            throw ACCESS_DENIED.exception();
        }

        return storageService.getUserByUserId(codeDetail.getUserId())
                .compose(user -> getOAuth2AccessToken(clientId, user))
                .compose(storageService::putAccessToken)
                .onSuccess(storedAccessToken -> event.response().send(storedAccessToken.toJson().encode()));
    }

    private Future<OAuth2AccessToken> getOAuth2AccessToken(String clientId, OAuth2User user) {
        Promise<OAuth2AccessToken> promise = Promise.promise();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(clientId, user.getUsername());
        promise.complete(accessToken);
        return promise.future();
    }
}
