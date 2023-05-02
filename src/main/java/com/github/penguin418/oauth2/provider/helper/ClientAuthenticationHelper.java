package com.github.penguin418.oauth2.provider.helper;

import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ClientAuthenticationHelper {
    public static final String AUTHENTICATION_HEADER_NAME = "authorization";

    public Future<Void> tryClientAuthenticate(RoutingContext context, Oauth2Client clientDetail){
        Promise<Void> promise = Promise.promise();

        try {
            final String authHeader=context.request().getHeader(AUTHENTICATION_HEADER_NAME);
            if (authHeader != null){
                if (authWithClientSecretBasic(context, clientDetail))
                    promise.complete();
            }else{
                if (authWithClientSecretForm(context, clientDetail))
                    promise.complete();
            }
        } catch (NullPointerException | IllegalArgumentException e){
            promise.tryFail("");
        }
        return promise.future();
    }

    public String parseResourceOwnerAuthenticationHeader(RoutingContext context){
        final String authHeader=context.request().getHeader(AUTHENTICATION_HEADER_NAME);
        if (authHeader != null)
            return authHeader.substring("Bearer ".length()).trim();
        return null;
    }

    /**
     * Form Attribute 을 사용한 인증
     * @param context client_id, client_secret 이 들어있는 form
     * @param clientDetail
     * @return
     */
    private boolean authWithClientSecretForm(RoutingContext context, Oauth2Client clientDetail){
        final String clientId = context.request().getFormAttribute("client_id");
        final String clientSecret = context.request().getFormAttribute("client_secret");
        return (clientDetail.getClientId().equals(clientId) &&
                clientDetail.verified(clientSecret));
    }

    /**
     * Basic Authoirzation 을 사용한 인증
     * @param context Basic 타입 Authorization 헤더가 있는 요청
     * @param clientDetail
     * @return
     */
    private boolean authWithClientSecretBasic(RoutingContext context, Oauth2Client clientDetail){
        String[] idSecret = parseClientAuthenticationHeader(context);
        return (clientDetail.getClientId().equals(idSecret[0]) &&
                clientDetail.verified(idSecret[1]));
    }

    private String[] parseClientAuthenticationHeader(RoutingContext context){
        final String authHeader=context.request().getHeader(AUTHENTICATION_HEADER_NAME);
        final String credential = authHeader.substring("Basic ".length()).trim();
        final byte[] credDecoded = Base64.getDecoder().decode(credential);

        final String credPairs = new String(credDecoded, StandardCharsets.UTF_8); // credPairs = username:password
        return credPairs.split(":", 2);
    }
}
