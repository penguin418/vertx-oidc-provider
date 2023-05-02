package com.github.penguin418.oauth2.provider.helper;

import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ClientAuthenticationHelperTest {

    @Test
    public void testTryClientAuthenticate() {
        // given
        Oauth2Client clientDetail = Mockito.mock(Oauth2Client.class);
        RoutingContext context = Mockito.mock(RoutingContext.class);
        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(context.request().getHeader("authorization")).thenReturn("Basic " + "hh");//dXNlcm5hbWU6cGFzc3dvcmQK");
        Mockito.when(clientDetail.getClientId()).thenReturn("username");
        Mockito.when(clientDetail.verified("password")).thenReturn(true);
        ClientAuthenticationHelper helper = new ClientAuthenticationHelper();

        // when
        Future<Void> future = helper.tryClientAuthenticate(context, clientDetail);

        // then
        future.onComplete(result->{
            assertTrue(result.succeeded());
        });
    }

    @Test
    public void testParseResourceOwnerAuthenticationHeader() {
        // given
        RoutingContext context = Mockito.mock(RoutingContext.class);
        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);
        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(context.request().getHeader("authorization")).thenReturn("Bearer " + "access_token");
        ClientAuthenticationHelper helper = new ClientAuthenticationHelper();

        // when
        String result = helper.parseResourceOwnerAuthenticationHeader(context);

        // then
        assertEquals("access_token", result);
    }

}