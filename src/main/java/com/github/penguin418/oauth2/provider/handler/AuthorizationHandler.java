package com.github.penguin418.oauth2.provider.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class AuthorizationHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String login_uri;

    public AuthorizationHandler(Vertx vertx, String login_uri) {
        this.vertx = vertx;
        this.login_uri = login_uri;
    }

    @Override
    public void handle(RoutingContext event) {

    }
}
