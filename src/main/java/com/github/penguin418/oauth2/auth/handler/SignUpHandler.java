package com.github.penguin418.oauth2.auth.handler;

import com.github.penguin418.oauth2.auth.dto.SignUpRequest;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

public class SignUpHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final ThymeleafUtil thymeleafUtil;
    private final String signup_uri;
    private final OAuth2StorageService storageService;

    public SignUpHandler(Vertx vertx, String signup_uri) {
        this.vertx = vertx;
        this.thymeleafUtil = new ThymeleafUtil(vertx);
        this.storageService = OAuth2StorageService.createProxy(vertx);
        this.signup_uri = signup_uri;
    }

    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.GET)) {
            thymeleafUtil.render(event, signup_uri);
        } else if (event.request().method().equals(HttpMethod.POST)) {
            handlePostRequest(event);
        } else {
            event.fail(INVALID_REQUEST.exception());
        }
    }

    private void handlePostRequest(RoutingContext event) {
        SignUpRequest request = new SignUpRequest(event);
        OAuth2User user = new OAuth2User(request.getUsername(), request.getPassword());
        storageService.putUser(user.encrypt()).onSuccess(newUser->{
            event.response().send("success");
        }).onFailure(event::fail);

    }
}
