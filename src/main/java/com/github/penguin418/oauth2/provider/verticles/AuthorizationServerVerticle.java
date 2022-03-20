package com.github.penguin418.oauth2.provider.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;

import com.github.penguin418.oauth2.provider.handler.*;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Slf4j
public class AuthorizationServerVerticle extends AbstractVerticle {
    private final String authorization_uri = "/oauth2/authorize";
    private final String token_uri = "/oauth2/token";
    private final String user_info_uri = "/oauth2/user_info";

    private final String login_uri = "/oauth2/login";
    private final String permit_uri = "/oauth2/permit";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SessionStore sessionStore = SessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(sessionStore).setCookieSameSite(CookieSameSite.STRICT);

        Router router = Router.router(vertx);
        router.route().handler(sessionHandler);
        router.route().handler(BodyHandler.create());
        router.route().handler(StaticHandler.create().setCachingEnabled(false));
        router.route(authorization_uri).handler(new AuthorizationHandler(vertx, login_uri));
        router.route(token_uri).handler(new TokenHandler(vertx));
        router.route(user_info_uri).handler(new UserInfoHandler(vertx));
        router.route(login_uri).handler(new LoginHandler(vertx, login_uri, permit_uri));
        router.route(permit_uri).handler(new PermitHandler(vertx, login_uri));
        vertx.createHttpServer().requestHandler(router).listen(8888);
        startPromise.complete();
    }
}
