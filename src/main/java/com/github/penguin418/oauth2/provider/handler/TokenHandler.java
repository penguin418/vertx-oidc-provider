package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.dto.PermitRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.exception.AuthException;
import com.github.penguin418.oauth2.provider.helper.ClientAuthenticationHelper;
import com.github.penguin418.oauth2.provider.helper.CodeChallengeHelper;
import com.github.penguin418.oauth2.provider.model.OAuth2AccessToken;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import com.github.penguin418.oauth2.provider.model.constants.VertxConstants;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import static com.github.penguin418.oauth2.provider.exception.AuthError.ACCESS_DENIED;
import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

@Slf4j
public class TokenHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String permit_uri;
    private final OAuth2StorageService storageService;
    private final ThymeleafUtil thymeleafUtil;

    private static final ClientAuthenticationHelper clientAuthHelper = new ClientAuthenticationHelper();

    public TokenHandler(Vertx vertx, String permit_uri) {
        this.vertx = vertx;
        this.permit_uri = permit_uri;
        this.storageService = OAuth2StorageService.createProxy(vertx);
        this.thymeleafUtil = new ThymeleafUtil(vertx);
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
        final String formGrantType = event.request().getFormAttribute("grant_type");
        final String queryGrantType = event.request().getParam("grant_type");
        String grantType = formGrantType != null ? formGrantType : queryGrantType;
        if (grantType.equals("authorization_code")) {
            handlePostAuthorizationCodeRequest(event);
        } else if (grantType.equals("refresh_token")) {
            handlePostRefreshTokenRequest(event);
        } else if (grantType.equals("password")) {
            handlePostPasswordGrantRequest(event);
        } else {
            // 현재는 지원하지 않는 기능
            event.fail(INVALID_REQUEST.exception());
        }
    }

    private void handlePostPasswordGrantRequest(RoutingContext event) {
        final String username = event.request().getParam("username");
        final String password = event.request().getParam("password");
        final String clientId = event.request().getParam("client_id");
        final String clientSecret = event.request().getParam("client_secret");
        final String scope = event.request().getParam("scope");

        storageService.getClientByClientId(clientId)
                .compose(clientDetail -> verifyClient(clientDetail, clientSecret))
                .compose(onVerified -> storageService.getUserByUsername(username))
                .compose(userDetail -> verifyUser(userDetail, password))
                .compose(onVerified -> sendBackPasswordGrantedAccessToken(event, clientId, username))
                .onFailure(fail -> {
                    if (fail instanceof AuthException) {
                        AuthException authFail = (AuthException) fail;
                        switch (authFail.getErrorMsg()) {
                            case UNAUTHORIZED_CLIENT:
                                redirectToPermissionGrantPage(event, clientId, scope);
                                break;
                            default:
                                event.fail(ACCESS_DENIED.exception());
                        }
                    } else
                        event.fail(ACCESS_DENIED.exception());
                });
    }

    private Future<Void> verifyClient(Oauth2Client clientDetail, String clientSecret) {
        log.info("verify client");
        Promise<Void> promise = Promise.promise();
        if (clientDetail.verified(clientSecret)) {
            promise.complete();
        } else {
            promise.fail(AuthError.ACCESS_DENIED_LOGIN_FAILURE.exception());
        }
        return promise.future();
    }

    private Future<Void> verifyUser(OAuth2User userDetail, String password) {
        log.info("verify user");
        Promise<Void> promise = Promise.promise();
        if (userDetail.verified(password)) {
            promise.complete();
        } else {
            promise.fail(AuthError.ACCESS_DENIED_LOGIN_FAILURE.exception());
        }
        return promise.future();
    }

    private void handlePostRefreshTokenRequest(RoutingContext event) {
        /* required */
        final String refreshToken = event.request().getFormAttribute("refresh_token");
        /* optional */
        final String scope = event.request().getFormAttribute("scope");

        storageService.getAccessTokenDetailByRefreshToken(refreshToken)
                .onSuccess(token -> refreshToken(event, token, scope))
                .onFailure(ignored -> event.fail(INVALID_REQUEST.exception()));
    }

    private void refreshToken(final RoutingContext event, final OAuth2AccessToken token, final String newScope) {
        if (newScope != null) {
            // TODO: 기존 토큰과 scope 같은지 검사 후 다르면 실패처리 (삭제 전에 수행)
        }
        final String oldAccessToken = token.getAccessToken();
        token.refresh();

        storageService.deleteAccessToken(oldAccessToken)
                .compose(ignored -> storageService.putAccessToken(token))
                .compose(ignored -> event.response().send(token.toJson().encode()))
                .onFailure(e -> event.fail(AuthError.SERVER_ERROR.withDetail(e.getMessage()).exception()));
    }

    private void handlePostAuthorizationCodeRequest(RoutingContext event) {
        /* required */
        final String code = event.request().getFormAttribute("code");
        /* required */
        final String redirectUri = event.request().getFormAttribute("redirect_uri");
        /* required */
        final String clientId = event.request().getFormAttribute("client_id");
        /* optional */
        final String codeVerifier = event.request().getFormAttribute("code_verifier");

        storageService.getClientByClientId(clientId)
                .compose(clientDetail -> clientAuthHelper.tryAuthenticate(event, clientDetail))
                .compose(onVerified -> storageService.getCodeDetail(code))
                .compose(codeDetail -> checkCodeChallenge(codeDetail, codeVerifier))
                .compose(codeDetail -> sendBackAccessToken(event, code, redirectUri, clientId, codeDetail))
                .onFailure(event::fail);
    }

    private Future<OAuth2Code> checkCodeChallenge(OAuth2Code oAuth2Code, String codeVerifier) {
        Promise<OAuth2Code> promise = Promise.promise();
        if (oAuth2Code.hasCodeChallenge()) {
            // check required
            if (codeVerifier != null) {
                final String challenge = oAuth2Code.getCodeChallenge();
                final String method = oAuth2Code.getCodeChallengeMethod();
                CodeChallengeHelper codeChallengeHelper = new CodeChallengeHelper();
                if (codeChallengeHelper.verifyCodeChallenge(challenge, method, codeVerifier)) {
                    promise.complete(oAuth2Code);
                } else {
                    promise.fail(AuthError.ACCESS_DENIED_VERIFIER_DOES_NOT_MATCH.exception());
                }
            } else {
                // has missing parameter (codeVerifier)
                promise.fail(AuthError.INVALID_REQUEST.exception());
            }
        } else {
            promise.complete(oAuth2Code);
        }
        return promise.future();
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
                .onSuccess(storedAccessToken -> event.response().send(storedAccessToken.toJson().encode()))
                .onFailure(e -> event.fail(AuthError.SERVER_ERROR.withDetail(e.getMessage()).exception()));
    }

    private Future<OAuth2AccessToken> sendBackPasswordGrantedAccessToken(RoutingContext event, String clientId, String username) {
        return storageService.getUserByUsername(username)
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


    // common error handling
    private Future<Void> redirectToPermissionGrantPage(RoutingContext event, String clientId, String scope) {
        String[] scopes = scope.split(" ");
        log.info("redirectToPermissionGrantPage");
        Promise<Void> promise = Promise.promise();
        PermitRequest permitRequest = new PermitRequest(clientId, scopes);
        event.session().put(PermitRequest.SESSION_STORE_NAME, permitRequest);
        final JsonObject data = new JsonObject().put("permit_uri", permit_uri).put("scopes", scopes);
        event.session().put(VertxConstants.RETURN_URL, event.request().uri());
        thymeleafUtil.render(event, data, permit_uri);
        promise.complete();
        return promise.future();
    }
}
