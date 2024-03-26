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
 * HPC 서버 통신용 WebClientFactory
 */
@Slf4j
@Component
public class HPCWebClientFactory implements WebClientFactory {
    private final WebClient webClient;
    private final MediaType sendMediaType;
    private final MediaType recvMediaType;

    @Autowired
    public HPCWebClientFactory() {
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
