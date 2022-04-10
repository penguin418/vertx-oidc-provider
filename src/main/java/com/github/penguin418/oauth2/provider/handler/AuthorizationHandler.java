package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.validation.AuthorizationRequestValidation;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

@Slf4j
public class AuthorizationHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String login_uri;

    private final AuthorizationRequestValidation validator = new AuthorizationRequestValidation();
    private final OAuth2StorageService storageService;

    public AuthorizationHandler(Vertx vertx, String login_uri) {
        this.vertx = vertx;
        this.login_uri = login_uri;
        this.storageService = OAuth2StorageService.createProxy(vertx);
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

    private void handleCodeGrant(RoutingContext event, AuthorizationRequest request) {
        storageService.getClientByClientId(request.getClientId())
                .onSuccess(client -> {
                    log.info("code grant request from client(client_id={})", client.getClientId());
                    String redirectionUri = request.getRedirectUri();
                    if (redirectionUri == null) {
                        request.setRedirectUri(client.getRedirectUris().get(0));
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else if (client.getRedirectUris().contains(redirectionUri)) {
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else {
                        AuthError error = INVALID_REDIRECT_URI.withDetail(redirectionUri);
                        if (request.getState() != null) {
                            log.info("state is null");
                            error.withState(request.getState());
                        }
                        event.fail(error.exception());
                    }
                }).onFailure(fail -> {
                    event.fail(UNAUTHORIZED_CLIENT.exception());
                });
    }

    private void handleImplicitGrant(RoutingContext event, AuthorizationRequest request) {
        storageService.getClientByClientId(request.getClientId())
                .onSuccess(client -> {
                    log.info("implicit grant request from client(client_id={})", client.getClientId());
                    String redirectionUri = request.getRedirectUri();
                    if (redirectionUri == null) {
                        request.setRedirectUri(client.getRedirectUris().get(0));
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else if (client.getRedirectUris().contains(redirectionUri)) {
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else {
                        AuthError error = INVALID_REDIRECT_URI.withDetail(redirectionUri);
                        if (request.getState() != null) {
                            log.info("state is null");
                            error.withState(request.getState());
                        }
                        event.fail(error.exception());
                    }
                }).onFailure(fail -> {
                    event.fail(UNAUTHORIZED_CLIENT.exception());
                });
    }


}
