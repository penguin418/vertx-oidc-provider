package com.github.penguin418.oauth2.provider.handler;

import com.github.penguin418.oauth2.provider.dto.PermitRequest;
import com.github.penguin418.oauth2.provider.model.OAuth2Permission;
import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import com.github.penguin418.oauth2.provider.util.ThymeleafUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

@Slf4j
public class PermitHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final String permit_uri;
    private final String login_uri;
    private final ThymeleafUtil thymeleafUtil;
    private final OAuth2StorageService storageService;

    public PermitHandler(Vertx vertx, String permit_uri, String login_uri) {
        this.vertx = vertx;
        this.permit_uri = permit_uri;
        this.login_uri = login_uri;
        this.thymeleafUtil = new ThymeleafUtil(vertx);
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }


    @Override
    public void handle(RoutingContext event) {
         if (event.request().method().equals(HttpMethod.POST)) {
            handlePostRequest(event);
        } else event.fail(INVALID_REQUEST.exception());
    }

    private void handlePostRequest(RoutingContext event) {
        final PermitRequest permitRequest = event.session().get(PermitRequest.SESSION_STORE_NAME);
        final OAuth2User oAuth2User = OAuth2User.getLoggedInUser(event);
        if (permitRequest != null && oAuth2User!=null) {
            final String userId = oAuth2User.getUserId();
            final String clientId = permitRequest.getClientId();
            final String[] scopes = permitRequest.getScopes();

            storageService.putPermission(new OAuth2Permission(userId, clientId, Arrays.asList(scopes)));
            String returnUrl = event.request().headers().get("Referer");
            event.session().remove(PermitRequest.SESSION_STORE_NAME);
            event.redirect(returnUrl);
        }
    }
}
