package com.github.penguin418.oauth2.provider.verticles;

import com.github.penguin418.oauth2.provider.model.OAuth2User;
import com.github.penguin418.oauth2.provider.model.Oauth2Client;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class DummyDataGeneratorVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        // initialize storage
        OAuth2StorageService storageService = OAuth2StorageService.createProxy(vertx);
        Oauth2Client client = new Oauth2Client(
                Arrays.asList("code"),
                Arrays.asList("login"),
                Arrays.asList("https://oauth.pstmn.io/v1/callback"));
        log.info("new dummy client={}", client.toJson());
        storageService.putClient(client.encrypt());

        OAuth2User user = new OAuth2User(
                "username",
                "password"
        );
        log.info("new user={}", user.toJson());
        storageService.putUser(user.encrypt());

        startPromise.complete();
    }
}
