package com.github.penguin418.oauth2.provider.filter;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthSessionHandler extends AuthenticationHandlerImpl<AuthenticationProvider> {
    private final String login_uri;

    public AuthSessionHandler(AuthenticationProvider authProvider, String login_uri) {
        super (authProvider);
        this.login_uri = login_uri;
    }

    @Override
    public void authenticate(RoutingContext context, Handler<AsyncResult<User>> handler) {
//        if (context.request().uri().contains(login_uri) && context.request().method().equals(HttpMethod.POST)){
//            MultiMap map = context.request().formAttributes();
//            JsonObject loginInfo = new JsonObject();
//            loginInfo.put("username", map.get("username"));
//            loginInfo.put("password", map.get("password"));
//
//            this.authProvider.authenticate(loginInfo, handler);
//        }else
            if (context.user() == null){
            log.info("not logged in");
            Session session = context.session();
            session.put("return_url", context.request().uri());
            handler.handle(Future.failedFuture(new HttpException(302, login_uri)));
        }else{
            log.info("user: {}", context.user().principal().encode());
            handler.handle(Future.succeededFuture(context.user()));
        }
    }

    @Override
    public boolean performsRedirect() {
        return true;
    }
}
