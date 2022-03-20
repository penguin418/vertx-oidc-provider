package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

public class LoginHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String login_uri;
    private final JsonObject login_uri_info;
    private final OAuth2StorageService storageService;
    private final ThymeleafUtil thymeleafUtil;


    public LoginHandler(Vertx vertx, String login_uri) {
        this.vertx = vertx;
        this.login_uri = login_uri;
        this.login_uri_info = new JsonObject().put("login_uri", login_uri);
        this.storageService = OAuth2StorageService.createProxy(vertx);
        this.thymeleafUtil = new ThymeleafUtil(vertx);
    }

    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.GET)) {
            getUserIfLoggedIn(event)
                    .onSuccess(user -> redirectToRequester(event))
                    .onFailure(fail -> thymeleafUtil.render(event, login_uri_info, login_uri));
        } else if (event.request().method().equals(HttpMethod.POST)) {
            handlePostRequest(event);
        } else event.fail(INVALID_REQUEST.exception());
    }

    private void handlePostRequest(RoutingContext event) {
        // json 으로 body 획득
        event.request().body().compose(rawBody -> {
                    JsonObject body = rawBody.toJsonObject();

                    // 로그인 된 경우, 로그인 정보 획득
                    return getUserIfLoginSuccess(event, body)
                            // session 이 존재하면 해당 정보를 통해 리다이렉션 여부 결정
                            .onSuccess(user -> redirectToRequester(event));
                })// 위의 모든 에러에 대해
                .onFailure(fail -> event.fail(ACCESS_DENIED.exception()));
    }


    private Future<OAuth2User> getUserIfLoggedIn(RoutingContext event) {
        final Promise<OAuth2User> promise = Promise.promise();
        OAuth2User user = OAuth2User.getLoggedInUser(event);

        if (user != null) {
            promise.complete(user);
            return promise.future();
        }

        promise.fail("not logged in");
        return promise.future();
    }


    private Future<OAuth2User> getUserIfLoginSuccess(RoutingContext event, JsonObject body) {
        Promise<OAuth2User> promise = Promise.promise();
        final String username = body.getString("username");
        final String password = body.getString("password");

        storageService.getUserByUsername(username).onSuccess(user -> {
            if (user.verified(password)) {
                user.addToSession(event);
                promise.complete(user);
            } else
                promise.fail("failed to login");
        });
        return promise.future();
    }

    private void redirectToRequester(RoutingContext event) {
        AuthorizationRequest oauth2Request = event.session().get(AuthorizationRequest.SESSION_STORE_NAME);
        if (oauth2Request != null) {
            OAuth2Code oAuth2code = new OAuth2Code(event.session().id(), oauth2Request.getRedirectUri());
            storageService.putCode(oAuth2code).onSuccess(code -> {
                String redirectUri = oauth2Request.getRedirectUri() + "?code=" + code.getCode();
                if (oauth2Request.getState() != null) redirectUri += "&state=" + oauth2Request.getState();
                event.redirect(redirectUri);
            }).onFailure(fail -> {
                event.fail(INVALID_REDIRECT_URI.exception());
            });
        }
    }
}
