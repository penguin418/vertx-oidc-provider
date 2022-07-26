package com.github.penguin418.oauth2.provider.verticles;

import com.github.penguin418.oauth2.auth.handler.SignUpHandler;
import com.github.penguin418.oauth2.dcr.handler.DynamicClientRegistrationHandler;
import com.github.penguin418.oauth2.provider.filter.AuthSessionHandler;
import com.github.penguin418.oauth2.provider.handler.*;
import com.github.penguin418.oauth2.provider.handler.AuthorizationHandler;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2AuthenticationProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.http.CookieSameSite;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationServerVerticle extends AbstractVerticle {
    private final String common_uri = "/*";
    // dynamic client register uri
    private final String dynamic_client_register_uri = "/register";
    private final String signup_uri = "/signup";

    // resource uri
    private final String user_info_uri = "/oauth2/user_info";
    // oauth2 uri
    private final String authorization_uri = "/oauth2/authorize";
    private final String token_uri = "/oauth2/token";
    private final String login_uri = "/oauth2/login";
    private final String permit_uri = "/oauth2/permit";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        Router router = Router.router(vertx);
        // session
        SessionStore sessionStore = SessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(sessionStore).setCookieSameSite(CookieSameSite.LAX);
        router.route().handler(sessionHandler);

        // body & static
        router.route().handler(BodyHandler.create());
        router.route().handler(StaticHandler.create().setCachingEnabled(false));

        // logging
        router.route().handler(ctx -> {
            String user = ctx.user() == null ? "" : OAuth2User.getLoggedInUser(ctx).toJson().encode();
            String headers = ctx.request().headers().toString();
            MultiMap forms = ctx.request().formAttributes();
            log.info("\n[2]sessionId: {}\nrequest  : {} {}\nuser     : {}\nheaders  : {}\nform     : {}", ctx.session().id(), ctx.request().method(), ctx.request().uri(), user, headers, forms);
            ctx.next();
        });

        // oauth2 resources
        router.route(user_info_uri).handler(new UserInfoHandler(vertx));
        router.route(signup_uri).handler(new SignUpHandler(vertx,signup_uri));

        // dynamic client register
        router.route(dynamic_client_register_uri).handler(new DynamicClientRegistrationHandler(vertx));

        // oauth2 token
        router.route(token_uri).handler(new TokenHandler(vertx, permit_uri));

        // auth process
        // before login
        OAuth2AuthenticationProvider authenticationProvider = new OAuth2AuthenticationProvider(vertx);
        router.get(login_uri).handler(new LoginHandler(vertx, login_uri, permit_uri));
        router.post(login_uri).handler(FormLoginHandler.create(authenticationProvider));
        // after login & redirect to login
        AuthenticationHandler authSessionHandler = new AuthSessionHandler(authenticationProvider, vertx, login_uri);
        router.route(common_uri).handler(authSessionHandler);
        router.route(authorization_uri).handler(new AuthorizationHandler(vertx, permit_uri));
        router.route(permit_uri).handler(new PermitHandler(vertx, permit_uri, login_uri));

        vertx.createHttpServer().requestHandler(router).listen(8888);
        startPromise.complete();
    }
}
