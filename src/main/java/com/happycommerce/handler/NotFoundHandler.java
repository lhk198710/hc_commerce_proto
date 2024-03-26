package com.happycommerce.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NotFoundHandler {
    public Mono<ServerResponse> notFound(ServerRequest request) {
        log.info("notFound");
        return ServerResponse.notFound().build();
    }
}
