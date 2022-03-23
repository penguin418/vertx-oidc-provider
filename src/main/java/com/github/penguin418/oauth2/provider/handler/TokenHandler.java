package com.github.penguin418.oauth2.provider.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import static com.github.penguin418.oauth2.provider.exception.AuthError.ACCESS_DENIED;
import static com.github.penguin418.oauth2.provider.exception.AuthError.INVALID_REQUEST;

public class TokenHandler implements Handler<RoutingContext> {
    private final Vertx vertx;

    public TokenHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handle(RoutingContext event) {
//        if (event.request().method().equals(HttpMethod.POST)){
////            handlePostRequest(event);
//        }else{
//            event.fail(INVALID_REQUEST.exception());
//        }
    }


//    private void handlePostRequest(RoutingContext event) {
//        // json 으로 body 획득
//        event.request().body().compose(rawBody -> {
//                    JsonObject body = rawBody.toJsonObject();
//
//                    // 로그인 된 경우, 로그인 정보 획득
//                    return getUserIfLoginSuccess(event, body)
//                            // session 이 존재하면 해당 정보를 통해 리다이렉션 여부 결정
//                            .onSuccess(user -> redirectToRequester(event));
//                })// 위의 모든 에러에 대해
//                .onFailure(fail -> event.fail(ACCESS_DENIED.exception()));
//    }
}
