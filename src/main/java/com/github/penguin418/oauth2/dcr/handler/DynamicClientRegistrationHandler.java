package com.github.penguin418.oauth2.dcr.handler;

import com.github.penguin418.oauth2.dcr.dto.ClientRegistrationRequest;
import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Arrays;

import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

public class DynamicClientRegistrationHandler implements Handler<RoutingContext> {
    private final Vertx vertx;
    private final OAuth2StorageService storageService;

    public DynamicClientRegistrationHandler(Vertx vertx){
        this.vertx = vertx;
        this.storageService = OAuth2StorageService.createProxy(vertx);
    }

    @Override
    public void handle(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.POST)){
            this.handleClientRegistrationRequest(event);
        }else{
            event.fail(INVALID_REQUEST.exception());
        }
    }

    private void handleClientRegistrationRequest(RoutingContext event){
        ClientRegistrationRequest request = new ClientRegistrationRequest(event);

        Oauth2Client client = new Oauth2Client(
                request.getResponseTypes(),
                Arrays.asList(request.getScope().split(",")),
                request.getRedirectUris());
        String response = Json.encode(client);
        storageService.putClient(client.encrypt())
                .onSuccess(clientDetail->{
                    event.response().send(response);
                }).onFailure(fail -> {
                    event.fail(INVALID_REQUEST.exception());
                });
    }
}
