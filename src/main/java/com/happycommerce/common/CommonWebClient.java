package com.happycommerce.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CommonWebClient {
    private final ObjectMapper objectMapper;

    public CommonWebClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> Mono<T> sendDataGetApplicationJson(WebClient webClient, String uri, String formData, Class<T> responseClass, MediaType sendMediaType, MediaType recvMediaType, String traceId) {
        return webClient
                .get()
                .uri(uri)
                .accept(recvMediaType)
                .exchangeToMono(response -> {
                    //성공요청이 아닌 경우
                    if (!response.statusCode().is2xxSuccessful()) {
                        log.error("Response status code error. statusCode={}, traceId={}", response.statusCode(), traceId);
                        return Mono.error(new Exception("Response status code error. statusCode=" + response.statusCode()));
                    }
                    return response.bodyToMono(String.class);
                })
                .doOnNext(body -> log.info("receive succeed. body={}, traceId={}", body, traceId))
                .flatMap(body -> {
                    try {
                        return Mono.just(objectMapper.readValue(body, responseClass));
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException occurred. message={}, traceId={}", e, traceId);
                        return Mono.error(e);
                    }
                }).doOnSubscribe(subscription -> log.info("send start. request={}, response={}", formData, traceId));

    }

    public <T> Mono<T> sendDataPostApplicationJson(WebClient webClient, String uri, Object requestObject, Class<T> responseClass, MediaType sendMediaType, MediaType recvMediaType, String traceId) {
        return webClient
                .post()
                .uri(uri)
                .contentType(sendMediaType)
                .body(Mono.just(requestObject), requestObject.getClass())
                .accept(recvMediaType)
                .exchangeToMono(response -> {
                    //성공요청이 아닌 경우
                    if (!response.statusCode().is2xxSuccessful()) {
                        log.error("Response status code error. statusCode={}, traceId={}", response.statusCode(), traceId);
                        return Mono.error(new Exception("Response status code error. statusCode=" + response.statusCode()));
                    }
                    return response.bodyToMono(String.class);
                })
                .doOnNext(body -> log.info("receive succeed. body={}, traceId={}", body, traceId))
                .flatMap(body -> {
                    try {
                        return Mono.just(objectMapper.readValue(body, responseClass));
                    } catch (JsonProcessingException e) {
                        log.error("JsonProcessingException occurred. message={}, traceId={}", e, traceId);
                        return Mono.error(e);
                    }
                }).doOnSubscribe(subscription -> log.info("send start. request={}, response={}", requestObject, traceId));

    }
}
