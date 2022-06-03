package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2Permission;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.model.constants.VertxConstants;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import com.github.penguin418.oauth2.provider.validation.AuthorizationRequestValidation;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

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
        final AuthorizationRequest request = new AuthorizationRequest(event);

        if (request.isAuthorizationCodeGrantRequest()) {
            handleCodeGrant(event, request);
        } else if (request.isImplicitGrantRequest()) {
            handleImplicitGrant(event, request);
        } else event.fail(INVALID_REQUEST.exception());
    }

    private void handleImplicitGrant(RoutingContext event, AuthorizationRequest request) {

    }

    private void handleCodeGrant(RoutingContext event, AuthorizationRequest request) {
        storageService.getClientByClientId(request.getClientId())
                .onSuccess(client -> {
                    log.info("code grant request from client(client_id={})", client.getClientId());
                    checkPermissionThenRedirect(event, request);
                }).onFailure(fail -> {
                    event.fail(UNAUTHORIZED_CLIENT.exception());
                });
    }

    private void checkPermissionThenRedirect(RoutingContext event, AuthorizationRequest oauth2Request) {
        log.info("checkPermissionThenRedirect");
        final OAuth2User oAuth2User = OAuth2User.getLoggedInUser(event);
        if (oAuth2User == null) {
            log.warn("not logged in user entered auth grant process. session={}", event.session());
        } else if (oauth2Request != null) {
            final String userId = oAuth2User.getUserId();
            final String clientId = oauth2Request.getClientId();
            final String[] scopes = oauth2Request.getScope().split(" ");

            storageService.getPermissionByUserId(userId, clientId)
                    .onSuccess(permissions -> {
                        log.info("permissions: {}", permissions);

                        if (permissions == null) {
                            log.info("no permission");
                            redirectToPermissionGrantPage(event, oauth2Request);
                        } else if (permissions.getScopes().containsAll(Arrays.asList(scopes))) {
                            responseToRequest(event, oauth2Request, userId, permissions);
                        } else throw AuthError.INVALID_REQUEST.exception();
                    }).onFailure(fail -> redirectToPermissionGrantPage(event, oauth2Request));
        }
    }

    private void responseToRequest(RoutingContext event, AuthorizationRequest oauth2Request, String userId, OAuth2Permission permissions) {
        if (oauth2Request.isAuthorizationCodeGrantRequest()) {
            OAuth2Code oAuth2code = new OAuth2Code(event.session().id(), userId, oauth2Request.getRedirectUri());
            // PKCE
            if (oauth2Request.hasCodeChallenge()){
                oAuth2code.setCodeChallenge(oauth2Request.getCode_challenge(), oAuth2code.getCodeChallengeMethod());
            }
            responseToCodeGrantRequest(event, oauth2Request, oAuth2code);
        } else if (oauth2Request.isImplicitGrantRequest()) {

        }
    }

    private void responseToCodeGrantRequest(RoutingContext event, AuthorizationRequest oauth2Request, OAuth2Code oAuth2code) {
        log.info("redirectToReferer");
        storageService.putCode(oAuth2code).onSuccess(code -> {
            String redirectUri = oauth2Request.getRedirectUri() + "?code=" + code.getCode();
            if (oauth2Request.getState() != null) redirectUri += "&state=" + oauth2Request.getState();
            event.session().remove(AuthorizationRequest.SESSION_STORE_NAME);
            log.info("redirect_uri: {}", redirectUri);
            event.redirect(redirectUri);
        }).onFailure(fail -> {
            event.fail(INVALID_REDIRECT_URI.exception());
        });
    }

    private void redirectToPermissionGrantPage(RoutingContext event, AuthorizationRequest oauth2Request) {
        log.info("redirectToPermissionGrantPage");
        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, oauth2Request);
        final String[] scopes = oauth2Request.getScope().split(" ");
        final JsonObject data = new JsonObject().put("permit_uri", permit_uri).put("scopes", scopes);
        event.session().put(VertxConstants.RETURN_URL, event.request().uri());
        thymeleafUtil.render(event, data, permit_uri);
    }
}
