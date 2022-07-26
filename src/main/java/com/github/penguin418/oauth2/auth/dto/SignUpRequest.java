package com.github.penguin418.oauth2.auth.dto;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignUpRequest {
    private String username;
    private String password;
    private String passwordConfirm;

    public SignUpRequest(RoutingContext context){
        final MultiMap map = context.queryParams().contains("response_type")
                ? context.request().params()
                : context.request().formAttributes();

        this.username = map.get("username");
        this.password = map.get("password");
        this.passwordConfirm = map.get("passwordConfirm");
    }
}
