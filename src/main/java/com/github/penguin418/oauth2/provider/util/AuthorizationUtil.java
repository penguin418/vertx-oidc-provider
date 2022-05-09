package com.github.penguin418.oauth2.provider.util;

import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AuthorizationUtil {
    private static final String AUTHENTICATION_HEADER_NAME = "authorization";

    public static String[] parseAuthentication(RoutingContext context){
        final String authHeader=context.request().getHeader(AUTHENTICATION_HEADER_NAME);
        final String credential = authHeader.substring("Basic".length()).trim();
        final byte[] credDecoded = Base64.getDecoder().decode(credential);

        final String credPairs = new String(credDecoded, StandardCharsets.UTF_8); // credPairs = username:password
        return credPairs.split(":", 2);
    }
}
