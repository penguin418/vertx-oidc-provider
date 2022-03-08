package com.github.penguin418.oauth2.provider;

import com.github.penguin418.oauth2.provider.verticles.AuthorizationServerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class LauncherVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(new AuthorizationServerVerticle());
        startPromise.complete();
    }
}
