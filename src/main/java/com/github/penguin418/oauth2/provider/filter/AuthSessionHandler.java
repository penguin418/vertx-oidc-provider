package com.github.penguin418.oauth2.provider.filter;

import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.AuthenticationHandler;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.ext.web.handler.RedirectAuthHandler;
import io.vertx.ext.web.handler.impl.AuthenticationHandlerImpl;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

@Slf4j
public class AuthSessionHandler extends AuthenticationHandlerImpl<AuthenticationProvider> implements AuthenticationHandler {
    private final String login_uri;
    private final OAuth2StorageService storageService;


    public AuthSessionHandler(AuthenticationProvider authProvider, Vertx vertx, String login_uri) {
        super (authProvider);
        this.login_uri = login_uri;
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }

    @Override
    public void authenticate(RoutingContext context, Handler<AsyncResult<User>> handler) {
        log.info("[auth - handler] - session: {}", context.session());
        if (context.user() == null){
            log.info("not logged in.");
            Session session = context.session();
            session.put("return_url", context.request().uri());
            handler.handle(Future.failedFuture(new HttpException(302, login_uri)));
        }else{
            log.info("logged in. user: {}", context.user().principal().encode());
            handler.handle(Future.succeededFuture(context.user()));
        }
    }

    @Override
    public boolean performsRedirect() {
        return true;
    }
}
