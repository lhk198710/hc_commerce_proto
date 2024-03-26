package com.happycommerce.router;

import com.happycommerce.handler.SecureHandler;
import com.happycommerce.interfaces.AdminHPCAuthReqDtoInfo;
import com.happycommerce.interfaces.AdminSecureApiInfo;
import com.happycommerce.interfaces.AdminSecureApiReqDtoInfo;
import com.happycommerce.interfaces.SecureApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

@Slf4j
@Component
public class SecureRouter {
    private final SecureHandler secureHandler;

    public SecureRouter(SecureHandler secureHandler) {
        this.secureHandler = secureHandler;
    }

    @Bean
    @SecureApiInfo
    public RouterFunction<ServerResponse> routeSecure() {
        RequestPredicate requestPredicate =  RequestPredicates.GET("/api/v1/secure").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, secureHandler::testHandler);
    }

    @Bean
    @AdminSecureApiInfo
    public RouterFunction<ServerResponse> routeAdminApiTest() {
        RequestPredicate requestPredicate =  RequestPredicates.GET("/api/admin/v1/get").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, secureHandler::testHandler2);
    }

    @Bean
    @AdminSecureApiReqDtoInfo
    public RouterFunction<ServerResponse> routeAdminApiRequestDtoTest() {
        RequestPredicate requestPredicate = RequestPredicates.POST("/api/admin/v1/dto").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, secureHandler::testRequestDataInsert);
    }

    @Bean
    @AdminSecureApiReqDtoInfo
    public RouterFunction<ServerResponse> routeAdminApiRequestDtoWithRollbackTest() {
        RequestPredicate requestPredicate = RequestPredicates.POST("/api/admin/v1/dto/rollback").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, secureHandler::testRequestDataInsertWithRollback);
    }

    @Bean
    @AdminHPCAuthReqDtoInfo
    public RouterFunction<ServerResponse> routeHPCCommTest() {
        RequestPredicate requestPredicate = RequestPredicates.POST("/api/admin/v1/hpc/get").and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(requestPredicate, secureHandler::testHPCComm);
    }
}
