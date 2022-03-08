package com.github.penguin418.oauth2.provider.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import com.github.penguin418.oauth2.provider.handler.*;


public class AuthorizationServerVerticle extends AbstractVerticle {
    private final String authorization_uri = "/oauth2/authorize";
    private final String token_uri = "/oauth2/token";
    private final String user_info_uri = "/oauth2/user_info";

    private final String login_uri = "/oauth2/login";
    private final String permit_uri = "/oauth2/permit";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(StaticHandler.create().setCachingEnabled(false));
        router.route(authorization_uri).handler(new AuthorizationHandler(vertx, login_uri));
        router.route(token_uri).handler(new TokenHandler(vertx));
        router.route(user_info_uri).handler(new UserInfoHandler(vertx));
        router.route(login_uri).handler(new LoginHandler(vertx));
        router.route(permit_uri).handler(new PermitHandler(vertx));
        vertx.createHttpServer().requestHandler(router).listen(8888);
        startPromise.complete();
    }
}
