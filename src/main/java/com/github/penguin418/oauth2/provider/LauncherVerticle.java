package com.github.penguin418.oauth2.provider;

import com.github.penguin418.oauth2.provider.verticles.AuthorizationServerVerticle;
import com.github.penguin418.oauth2.storage.memory.MemoryStorageVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Launcher;
import io.vertx.core.Promise;

public class LauncherVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.deployVerticle(new AuthorizationServerVerticle());
        vertx.deployVerticle(new MemoryStorageVerticle());
        startPromise.complete();
    }

    public static void main(String[] args) {
        Launcher.main(new String[]{"run", LauncherVerticle.class.getSimpleName()});
    }
}
