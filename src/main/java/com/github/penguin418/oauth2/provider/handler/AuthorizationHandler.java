package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.dto.PermitRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.exception.AuthException;
import com.github.penguin418.oauth2.provider.model.OAuth2AccessToken;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2Permission;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.model.constants.VertxConstants;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import com.github.penguin418.oauth2.provider.validation.AuthorizationRequestValidation;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Arrays;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

@Slf4j
public class AuthorizationHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    //    private final String login_uri;
    private final String permit_uri;

    private final AuthorizationRequestValidation validator = new AuthorizationRequestValidation();
    private final OAuth2StorageService storageService;
    private final ThymeleafUtil thymeleafUtil;

    public AuthorizationHandler(Vertx vertx, String permit_uri) {
        this.vertx = vertx;
        this.permit_uri = permit_uri;
        this.storageService = OAuth2StorageService.createProxy(vertx);
        this.thymeleafUtil = new ThymeleafUtil(vertx);
    }

    @Override
    public void handle(RoutingContext event) {
        // parse request
        final MultiMap params = event.queryParams().contains("response_type")
                ? event.request().params()
                : event.request().formAttributes();
        final AuthorizationRequest request = new AuthorizationRequest(params);

        if (request.isAuthorizationCodeGrantRequest()) {
            handleCodeGrant(event, request);
        } else if (request.isImplicitGrantRequest()) {
            handleImplicitGrant(event, request);
        } else {
            log.info("invalid request : + {}", request.toString());
            event.fail(INVALID_REQUEST.exception());}
    }

    private void handleImplicitGrant(RoutingContext event, AuthorizationRequest request) {
        log.info("implicit");
        final OAuth2User user = OAuth2User.getLoggedInUser(event);
        if (user == null) {
            log.warn("not logged in user entered auth grant process. session={}", event.session());
            event.fail(INVALID_REQUEST.exception());
        }else {
            storageService.getClientByClientId(request.getClientId())
                    .compose(client -> checkPermission(event, user, request))
                    .compose(permissions -> sendBackAccessToken(event, request, user))
                    .onFailure(fail -> {
                        if (fail instanceof AuthException) {
                            AuthException authFail = (AuthException) fail;
                            switch (authFail.getErrorMsg()) {
                                case UNAUTHORIZED_CLIENT:
                                    redirectToPermissionGrantPage(event, request);
                                    break;
                                default:
                                    event.fail(ACCESS_DENIED.exception());
                            }
                        } else
                            event.fail(ACCESS_DENIED.exception());
                    });
        }
    }

    private void handleCodeGrant(RoutingContext event, AuthorizationRequest request) {
        final OAuth2User user = OAuth2User.getLoggedInUser(event);
        if (user == null) {
            log.warn("not logged in user entered auth grant process. session={}", event.session());
            event.fail(INVALID_REQUEST.exception());
        }else {
            storageService.getClientByClientId(request.getClientId())
                    .compose(client -> checkPermission(event, user, request))
                    .compose(permissions -> sendBackCode(event, request, user.getUserId(), permissions))
                    .onFailure(fail -> {
                        if (fail instanceof AuthException) {
                            AuthException authFail = (AuthException) fail;
                            switch (authFail.getErrorMsg()) {
                                case UNAUTHORIZED_CLIENT:
                                    redirectToPermissionGrantPage(event, request);
                                    break;
                                default:
                                    event.fail(ACCESS_DENIED.exception());
                            }
                        } else
                            event.fail(ACCESS_DENIED.exception());
                    });
        }
    }

    private Future<OAuth2Permission> checkPermission(RoutingContext event, @NonNull OAuth2User oAuth2User, @NonNull AuthorizationRequest oauth2Request) {
        log.info("code grant request from client(client_id={})", oauth2Request.getClientId());
        Promise<OAuth2Permission> promise = Promise.promise();

        final String userId = oAuth2User.getUserId();
        final String clientId = oauth2Request.getClientId();
        final String[] scopes = oauth2Request.getScope().split(" ");

        storageService.getPermissionByUserId(userId, clientId)
                .onSuccess(permissions -> {
                    log.info("permissions: {}", permissions);
                    if (permissions == null || !permissions.getScopes().containsAll(Arrays.asList(scopes))) {
                        log.info("not have enough permission");
                        promise.fail(UNAUTHORIZED_CLIENT.exception());
                    } else {
                        promise.complete(permissions);
                    }
                }).onFailure(fail -> promise.fail(UNAUTHORIZED_CLIENT.exception()));

        return promise.future();
    }

    private Future<Void> sendBackCode(RoutingContext event, AuthorizationRequest oauth2Request, String userId, OAuth2Permission permissions) {
        Promise<Void> promise = Promise.promise();
        OAuth2Code oAuth2code = new OAuth2Code(event.session().id(), userId, oauth2Request.getRedirectUri());
        // PKCE
        if (oauth2Request.hasCodeChallenge()) {
            oAuth2code.setCodeChallenge(oauth2Request.getCode_challenge(), oAuth2code.getCodeChallengeMethod());
        }
        log.info("sendBackCode");
        storageService.putCode(oAuth2code).onSuccess(code -> {
            String redirectUri = oauth2Request.getRedirectUri() + "?code=" + code.getCode();
            if (oauth2Request.getState() != null) redirectUri += "&state=" + oauth2Request.getState();
            event.session().remove(AuthorizationRequest.SESSION_STORE_NAME);
            log.info("redirect_uri: {}", redirectUri);
            event.redirect(redirectUri);
        }).onFailure(fail -> {
            event.fail(INVALID_REDIRECT_URI.exception());
        });
        promise.complete();
        return promise.future();
    }

    private Future<Void> redirectToPermissionGrantPage(RoutingContext event, AuthorizationRequest oauth2Request) {
        log.info("redirectToPermissionGrantPage");
        Promise<Void> promise = Promise.promise();
        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, oauth2Request);
        event.session().put(PermitRequest.SESSION_STORE_NAME, new PermitRequest(oauth2Request.getClientId(), oauth2Request.getScope().split(" ")));
        final String[] scopes = oauth2Request.getScope().split(" ");
        final JsonObject data = new JsonObject().put("permit_uri", permit_uri).put("scopes", scopes);
        event.session().put(VertxConstants.RETURN_URL, event.request().uri());
        thymeleafUtil.render(event, permit_uri, data);
        promise.complete();
        return promise.future();
    }

    private Future<OAuth2AccessToken> sendBackAccessToken(RoutingContext event, @NonNull AuthorizationRequest oauth2Request, OAuth2User user) {
        final String clientId = oauth2Request.getClientId();

        Instant now = Instant.now();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(clientId, user.getUsername());

        return storageService.putAccessToken(accessToken)
                .onSuccess(storedAccessToken -> {
                    String redirectUri = oauth2Request.getRedirectUri() + "#access_token=" + accessToken.getAccessToken();
                    redirectUri += "&token_type=Bearer";
                    redirectUri += "&expires_in="+accessToken.getExpiresAt();
                    if (oauth2Request.getState() != null) redirectUri += "&state=" + oauth2Request.getState();
                    log.info("redirect_uri: {}", redirectUri);
                    event.redirect(redirectUri);

                })
                .onFailure(e -> event.fail(AuthError.SERVER_ERROR.withDetail(e.getMessage()).exception()));
    }
}
