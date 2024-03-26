package com.happycommerce.repository;

import com.happycommerce.entity.ProxyApiKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
class ProxyApiKeyRepositoryTest {
    @Autowired
    private ProxyApiKeyRepository proxyApiKeyRepository;

    private ProxyApiKey newEntity;
    private String apiKey = "apiKey";

    @BeforeEach
    public void setUp() {
        newEntity = ProxyApiKey.builder()
                .apiKey(apiKey)
                .regId("hk.lee")
                .seq(null)
                .useYn("Y")
                .updId("hk.lee")
                .clientCode("TCC")
                .clientName("TCN")
                .role("ROLE_MEMBER")
                .build();
    }

    @Test
    void findByApiKey() {
        Mono<ProxyApiKey> proxyApiKeyMono = proxyApiKeyRepository.findByApiKey("abc");
        ProxyApiKey proxyApiKey = proxyApiKeyMono.block();
        Assertions.assertNotNull(proxyApiKey);
    }

    @Test
    void insertApiKey() {
        StepVerifier
                .create(proxyApiKeyRepository.save(newEntity))
                .consumeNextWith(p -> {
                    log.info("saved data : {}", p);
                    Assertions.assertEquals(p.getApiKey(), apiKey);
                    newEntity.setSeq(p.getSeq());
                })
                .verifyComplete();
    }

    @Test
    void deleteApiKey() {
        StepVerifier
                .create(proxyApiKeyRepository.deleteByApiKey(apiKey)).verifyComplete();
    }
}