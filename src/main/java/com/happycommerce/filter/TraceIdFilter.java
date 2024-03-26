package com.happycommerce.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(1) // 필터 순서 지정
public class TraceIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = generateRequestId(); // trace ID 생성 로직을 구현
        exchange.getAttributes().put("traceId", traceId); // Request ID를 요청 컨텍스트에 저장
        return chain.filter(exchange);
    }

    /**
     * UUID Generator
     * @return
     */
    public String generateRequestId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}