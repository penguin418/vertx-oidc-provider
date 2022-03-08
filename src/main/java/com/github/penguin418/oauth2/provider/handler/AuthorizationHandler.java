package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.validation.AuthorizationRequestValidation;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

public class AuthorizationHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String login_uri;

    private final AuthorizationRequestValidation validator = new AuthorizationRequestValidation();

    public AuthorizationHandler(Vertx vertx, String login_uri) {
        this.vertx = vertx;
        this.login_uri = login_uri;
    }

    @Override
    public void handle(RoutingContext event) {
        // 입력값 검사
        if (!validator.isValidCodeAuthorizationRequest(event))
            event.fail(INVALID_REQUEST.exception());

        final HttpServerRequest request = event.request();
        if (request.method().equals(HttpMethod.POST))
            handlePost(request);
        else if (request.method().equals(HttpMethod.GET))
            handleGet(request);
        else event.fail(INVALID_REQUEST.exception());
    }

    private void handlePost(HttpServerRequest request) {

    }

    private void handleGet(HttpServerRequest request) {
    }
}
