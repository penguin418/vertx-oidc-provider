package com.github.penguin418.oauth2.provider.validation;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.net.URI;
import java.net.URISyntaxException;

public class AuthorizationRequestValidation {

    public boolean isValidCodeAuthorizationRequest(RoutingContext event) {
        return hasAllowedFormat(event)
                && hasAllowedResponseType(event, "code")
                && hasAllowedRedirectUri(event)
                && hasAllowedScope(event)
                && hasAllowedState(event);
    }

    private boolean hasAllowedState(RoutingContext event) {
        final String key = "state";
        if (!event.queryParams().contains(key)) return true;
        return BasicABNFComparator.isVSCHAR(event.queryParams().get(key));
    }

    /**
     * format 검사<br>
     * format는 반드시 application/x-www-form-urlencoded 여야 함.
     *
     * @param event
     * @return
     */
    private boolean hasAllowedFormat(RoutingContext event) {
        if (event.request().method().equals(HttpMethod.GET)) return true;
        final String key = "content-type";
        return event.request().getHeader(key).equalsIgnoreCase("application/x-www-form-urlencoded");
    }

    /**
     * response_type 검사<br>
     * response_type는 반드시 다음 중 하나.<br>
     * code, password
     *
     * @param event
     * @param responseType
     * @return
     */
    private boolean hasAllowedResponseType(RoutingContext event, String responseType) {
        final String key = "response_type";
        return event.queryParams().contains(key) && event.queryParams().get(key).equals(responseType);
    }

    /**
     * redirect_uri 검사<br>
     * redirect_uri는 선택. 하지만 존재하는 경우 반드시 절대 경로여야 함.
     *
     * @param event
     * @return
     */
    private boolean hasAllowedRedirectUri(RoutingContext event) {
        final String key = "redirect_uri";
        try {
            if (event.queryParams().contains(key)) return true;
            return new URI(event.queryParams().get(key)).isAbsolute();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    /**
     * scope 검사<br>
     * scope는 선택, 대소문자 구별되며, 공백으로 나뉨<br>
     *
     * @param event
     * @return
     */
    private boolean hasAllowedScope(RoutingContext event) {
        final String key = "scope";
        if (!event.queryParams().contains(key)) return true;
        return BasicABNFComparator.isNQCHAR(event.queryParams().get(key));
    }
}
