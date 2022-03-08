package com.github.penguin418.oauth2.provider.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class PermitHandler implements Handler<RoutingContext> {
    private final Vertx vertx;

    public PermitHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext event) {

    }
}
