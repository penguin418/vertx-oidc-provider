package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.AuthorizationRequest;
import com.github.penguin418.oauth2.provider.exception.AuthError;
import com.github.penguin418.oauth2.provider.model.OAuth2Code;
import com.github.penguin418.oauth2.provider.model.OAuth2Permission;
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
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.github.penguin418.oauth2.provider.exception.AuthError.*;

@Slf4j
public class LoginHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String login_uri;
    private final String permit_uri;
    private final JsonObject login_uri_info;
    private final OAuth2StorageService storageService;
    private final ThymeleafUtil thymeleafUtil;


    public LoginHandler(Vertx vertx, String login_uri, String permit_uri) {
        this.vertx = vertx;
        this.login_uri = login_uri;
        this.permit_uri = permit_uri;
        this.login_uri_info = new JsonObject().put("login_uri", login_uri);
        this.storageService = OAuth2StorageService.createProxy(vertx);
        this.thymeleafUtil = new ThymeleafUtil(vertx);
    }

    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.GET)) {
            thymeleafUtil.render(event,  login_uri, login_uri_info);

//            getUserIfLoggedIn(event)
//                    .onSuccess(user -> checkPermissionThenRedirect(event))
//                    .onFailure(fail -> thymeleafUtil.render(event, login_uri_info, login_uri));
        }
//        else if (event.request().method().equals(HttpMethod.POST)) {
//            handlePostRequest(event);
//        }
        else event.fail(INVALID_REQUEST.exception());
    }

//    private void handlePostRequest(RoutingContext event) {
//        log.info("LOGIN POST");
//
//        // json 으로 body 획득
//        final String username = event.request().getFormAttribute("username");
//        final String password = event.request().getFormAttribute("password");
//        // 로그인 된 경우, 로그인 정보 획득
//        getUserIfLoginSuccess(event, username, password)
//                // session 이 존재하면 해당 정보를 통해 리다이렉션 여부 결정
//                .onSuccess(user -> checkPermissionThenRedirect(event))
//                // 위의 모든 에러에 대해
//                .onFailure(fail -> event.fail(ACCESS_DENIED.exception()));
//    }


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


//    private Future<OAuth2User> getUserIfLoginSuccess(RoutingContext event, final String username, final String password) {
//        Promise<OAuth2User> promise = Promise.promise();
//        log.info("getUserIfLoginSuccess");
//        storageService.getUserByUsername(username).onSuccess(user -> {
//            log.info("user: {}", user.toJson().encode());
//            if (user.verified(password)) {
////                user.addToSession(event);
//                promise.complete(user);
//            } else
//                promise.fail(ACCESS_DENIED_LOGIN_FAILURE.exception());
//        }).onFailure(fail -> {
//            promise.fail(ACCESS_DENIED_LOGIN_FAILURE.exception());
//        });
//        return promise.future();
//    }

//    private void checkPermissionThenRedirect(RoutingContext event) {
//        log.info("checkPermissionThenRedirect");
//        final AuthorizationRequest oauth2Request = event.session().get(AuthorizationRequest.SESSION_STORE_NAME);
//        final OAuth2User oAuth2User = OAuth2User.getLoggedInUser(event);
//        if (oAuth2User == null) {
//
//        }else if (oauth2Request != null) {
//            final String userId = oAuth2User.getUserId();
//            final String clientId = oauth2Request.getClientId();
//            final String[] scopes = oauth2Request.getScope().split(" ");
//
//            storageService.getPermissionByUserId(userId, clientId)
//                    .onSuccess(permissions -> {
//                        if (permissions == null) {
//                            log.info("no permission");
//                            redirectToPermissionGrantPage(event, oauth2Request);
//                        } else if (permissions.getScopes().containsAll(Arrays.asList(scopes))) {
//                            responseToRequest(event, oauth2Request, userId, permissions);
//                        } else throw AuthError.INVALID_REQUEST.exception();
//                    }).onFailure(fail -> redirectToPermissionGrantPage(event, oauth2Request));
//        }
//    }

//    private void responseToRequest(RoutingContext event, AuthorizationRequest oauth2Request, String userId, OAuth2Permission permissions) {
//        if (oauth2Request.isAuthorizationCodeGrantRequest()) {
//            OAuth2Code oAuth2code = new OAuth2Code(event.session().id(), userId, oauth2Request.getRedirectUri());
//            responseToCodeGrantRequest(event, oauth2Request, oAuth2code);
//        } else if (oauth2Request.isImplicitGrantRequest()) {
//
//        }
//    }

//    private void responseToCodeGrantRequest(RoutingContext event, AuthorizationRequest oauth2Request, OAuth2Code oAuth2code) {
//        log.info("redirectToReferer");
//        storageService.putCode(oAuth2code).onSuccess(code -> {
//            String redirectUri = oauth2Request.getRedirectUri() + "?code=" + code.getCode();
//            if (oauth2Request.getState() != null) redirectUri += "&state=" + oauth2Request.getState();
//            event.session().remove(AuthorizationRequest.SESSION_STORE_NAME);
//            event.redirect(redirectUri);
//        }).onFailure(fail -> {
//            event.fail(INVALID_REDIRECT_URI.exception());
//        });
//    }

    private void redirectToPermissionGrantPage(RoutingContext event, AuthorizationRequest oauth2Request) {
        log.info("redirectToPermissionGrantPage");
        final String[] scopes = oauth2Request.getScope().split(" ");
        final JsonObject data = new JsonObject().put("permit_uri", permit_uri).put("scopes", scopes);
        thymeleafUtil.render(event, permit_uri, data);
    }
}
