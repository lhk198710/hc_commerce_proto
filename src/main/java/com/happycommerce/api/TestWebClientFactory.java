package com.happycommerce.api;

import com.happycommerce.common.WebClients;
import com.happycommerce.interfaces.WebClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

/**
 * 특정 서버 도매인 별 WebClientFactory 생성
 * - 각 서버마다 설정해야 하는 timetout, mediayType 이 서로 상이 할 수 있는 관계로 분리하여 사용하기 위한 목적으로 생성
 */
@Slf4j
@Component
public class TestWebClientFactory implements WebClientFactory {
    private final WebClient webClient;
    private final MediaType sendMediaType;
    private final MediaType recvMediaType;

    @Autowired
    public TestWebClientFactory() {
        this.webClient = WebClients.client(3000, 5000);
        this.sendMediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        this.recvMediaType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
    }

    @Override
    public WebClient getClient() {
        return this.webClient;
    }

    @Override
    public MediaType getSendMediaType() {
        return this.sendMediaType;
    }

    @Override
    public MediaType getRecvMediaType() {
        return this.recvMediaType;
    }
}
