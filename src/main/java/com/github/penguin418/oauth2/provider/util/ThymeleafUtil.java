package com.github.penguin418.oauth2.provider.util;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;
import lombok.extern.slf4j.Slf4j;


import static io.vertx.ext.web.handler.StaticHandler.DEFAULT_WEB_ROOT;

@Slf4j
public class ThymeleafUtil {
    private final ThymeleafTemplateEngine engine;

    public ThymeleafUtil(Vertx vertx) {
        this.engine = ThymeleafTemplateEngine.create(vertx);
    }

    public Future<Buffer> render(RoutingContext context, JsonObject data, String path) {
        return engine.render(data, String.format("%s/%s.html", DEFAULT_WEB_ROOT, path)).onSuccess(result -> {
            context.response().end(result);
        }).onFailure(context::fail);
    }
}
