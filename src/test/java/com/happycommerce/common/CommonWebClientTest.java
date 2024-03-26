package com.happycommerce.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.happycommerce.api.HPCWebClientFactory;
import com.happycommerce.api.TestWebClientFactory;
import com.happycommerce.dto.HPCAuthRequestDto;
import com.happycommerce.dto.HPCAuthResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class CommonWebClientTest {
    @Autowired
    CommonWebClient commonWebClient;

    @Autowired
    TestWebClientFactory testWebClientFactory;

    @Autowired
    HPCWebClientFactory hpcWebClientFactory;

    /**
     * HTTP 통신 Custom client 테스트 목적(REST API 대비)
     * @throws JsonProcessingException
     */
    @Test
    void sendDataGetApplicationJsonTest() throws JsonProcessingException {
        Mono<String> testStr = commonWebClient.sendDataGetApplicationJson(testWebClientFactory.getClient(), "http://www.google.com","", String.class, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "test");
        log.info("String data : {}", testStr.block());
    }

    @Test
    void sendHPCDataGetApplicationJsonTest() throws JsonProcessingException {
        HPCAuthRequestDto hpcAuthRequestDto = HPCAuthRequestDto.builder()
                .trsDt("20240320")
                .trsTm("093232")
                .tracNo("1234567890")
                .reqUserId("lhk8710")
                .onlnId("lhk8710")
                .onlnPwd("1111")
                .build();

        Mono<HPCAuthResponseDto> hpcAuthRequestDtoMono = commonWebClient.sendDataPostApplicationJson(hpcWebClientFactory.getClient(),
                "https://dev-apin.happypointcard.co.kr:7243/processHpc",
                hpcAuthRequestDto, HPCAuthResponseDto.class, MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON, "test");
        log.info("Response data : {}", hpcAuthRequestDtoMono.block().hpcAuthResponseOf());
    }
}