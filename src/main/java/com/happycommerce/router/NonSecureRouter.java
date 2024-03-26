package com.happycommerce.router;

import com.happycommerce.enums.Role;
import com.happycommerce.handler.NonSecureHandler;
import com.happycommerce.interfaces.NonSecureApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;


@Slf4j
@Component
public class NonSecureRouter {
    private final NonSecureHandler nonSecureHandler;

    public NonSecureRouter(NonSecureHandler nonSecureHandler) {
        this.nonSecureHandler = nonSecureHandler;
    }

    @Bean
    @NonSecureApiInfo
    public RouterFunction<ServerResponse> routeNonSecure() {
        RequestPredicate requestPredicate =  RequestPredicates.GET("/api/v1/nonsecure").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, request -> nonSecureHandler.testHandler(request));
    }
}
