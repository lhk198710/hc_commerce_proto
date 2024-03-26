package com.happycommerce.interfaces;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public interface WebClientFactory {
    public WebClient getClient();
    public MediaType getSendMediaType();
    public MediaType getRecvMediaType();
}
