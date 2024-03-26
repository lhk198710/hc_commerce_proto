package com.happycommerce.handler;

import com.happycommerce.dto.TestResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class NonSecureHandler {
    public Mono<ServerResponse> testHandler(ServerRequest request) {
        log.info("Received request: /api/v1/nonsecure");
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new TestResponseDto("API KEY 인증 필요 없는 라우터 테스트")));
    }
}
