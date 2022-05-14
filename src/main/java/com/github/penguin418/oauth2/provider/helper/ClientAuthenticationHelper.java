package com.github.penguin418.oauth2.provider.helper;

import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClientAuthenticationHelper {
    private static final String AUTHENTICATION_HEADER_NAME = "authorization";

    public Future<Void> tryAuthenticate(RoutingContext context, Oauth2Client clientDetail){
        Promise<Void> promise = Promise.promise();
        try {
            if (authWithClientSecretBasic(context, clientDetail))
                promise.complete();
        } finally {
            promise.tryFail("");
        }
        return promise.future();
    }

    private boolean authWithClientSecretBasic(RoutingContext context, Oauth2Client clientDetail){
        String[] idSecret = parseAuthenticationHeader(context);
        return (clientDetail.getClientId().equals(idSecret[0]) &&
                clientDetail.verified(idSecret[1]));
    }

    private String[] parseAuthenticationHeader(RoutingContext context){
        final String authHeader=context.request().getHeader(AUTHENTICATION_HEADER_NAME);
        final String credential = authHeader.substring("Basic".length()).trim();
        final byte[] credDecoded = Base64.getDecoder().decode(credential);

        final String credPairs = new String(credDecoded, StandardCharsets.UTF_8); // credPairs = username:password
        return credPairs.split(":", 2);
    }
}
