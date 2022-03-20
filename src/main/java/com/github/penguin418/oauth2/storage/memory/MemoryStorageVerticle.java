package com.github.penguin418.oauth2.storage.memory;

import com.github.penguin418.oauth2.storage.memory.impl.MemoryOAuth2StorageService;
import com.github.penguin418.oauth2.provider.service.OAuth2StorageService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

import static com.github.penguin418.oauth2.provider.service.OAuth2StorageService.DEFAULT_STORAGE_SERVICE_ADDRESS;

public class MemoryStorageVerticle extends AbstractVerticle {
    private MessageConsumer<JsonObject> storageService;
    @Override
    public void start(Promise<Void> startPromise) {
        storageService =  new ServiceBinder(vertx)
                .setAddress(DEFAULT_STORAGE_SERVICE_ADDRESS)
                        .register(OAuth2StorageService.class, new MemoryOAuth2StorageService());
        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        storageService.unregister();
        stopPromise.complete();
    }
}
