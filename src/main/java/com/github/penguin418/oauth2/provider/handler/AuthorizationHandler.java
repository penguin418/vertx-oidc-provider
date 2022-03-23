package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.validation.AuthorizationRequestValidation;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

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
        // 입력값 검사
        if (event.queryParams().get("response_type").equals("code")) {
            if (!validator.isValidCodeAuthorizationRequest(event))
                event.fail(INVALID_REQUEST.exception());
            handleCodeGrant(event);
        } else event.fail(INVALID_REQUEST.exception());
    }

    private void handleCodeGrant(RoutingContext event) {
        final AuthorizationRequest request = AuthorizationRequest.fromQuery(event.request());
        storageService.getClientByClientId(request.getClientId())
                .onSuccess(client -> {
                    String redirectionUri = request.getRedirectUri();
                    if (redirectionUri == null) {
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else if (client.getRedirectUris().contains(redirectionUri)) {
                        event.session().put(AuthorizationRequest.SESSION_STORE_NAME, request);
                        event.redirect(login_uri);
                    } else {
                        AuthError error = INVALID_REDIRECT_URI.withDetail(redirectionUri);
                        if (request.getState() != null)
                            error.withState(request.getState());
                        event.fail(error.exception());
                    }
                }).onFailure(fail->{
                    event.fail(UNAUTHORIZED_CLIENT.exception());
                });
    }
}
