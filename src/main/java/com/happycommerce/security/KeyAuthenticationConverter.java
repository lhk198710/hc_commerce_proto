package com.happycommerce.security;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.happycommerce.entity.ProxyApiKey;
import com.happycommerce.repository.ProxyApiKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Request 요청에 대한 헤더 파싱 & 토큰 실제 검증
 * 검증 완료 후 Authentication 객체 반환
 */
@Component
@Slf4j
public class KeyAuthenticationConverter implements ServerAuthenticationConverter {
    private static final String API_KEY_HEADER_NAME = "Api-Key";

    private final AsyncLoadingCache<String, KeyAuthenticationToken> tokenCache;

    private final ProxyApiKeyRepository proxyApiKeyRepository;

    public KeyAuthenticationConverter(ProxyApiKeyRepository proxyApiKeyRepository) {
        this.proxyApiKeyRepository = proxyApiKeyRepository;
        this.tokenCache = Caffeine.newBuilder()
                .maximumSize(10_000) // 최대 캐시 크기
                .expireAfterAccess(60, TimeUnit.MINUTES) // 캐시 만료 시간
                .buildAsync(new DatabaseCacheLoader(proxyApiKeyRepository));
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange)
                .flatMap(serverWebExchange -> Mono.justOrEmpty(serverWebExchange.getRequest().getHeaders().get(API_KEY_HEADER_NAME)))
                .filter(headerValues -> !headerValues.isEmpty())
                .flatMap(headerValues -> lookup(headerValues.get(0)));
    }

    private Mono<KeyAuthenticationToken> lookup(final String apiKey) {
        CompletableFuture<KeyAuthenticationToken> keyAuthenticationTokenFuture = tokenCache.get(apiKey);
        try {
            log.debug("Key table cache : {}", keyAuthenticationTokenFuture.get());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        //return Mono.fromFuture(tokenCache.get(apiKey));
        return Mono.fromFuture(keyAuthenticationTokenFuture);
    }

    private static class DatabaseCacheLoader implements AsyncCacheLoader<String, KeyAuthenticationToken> {
        private final ProxyApiKeyRepository proxyApiKeyRepository;

        public DatabaseCacheLoader(ProxyApiKeyRepository proxyApiKeyRepository) {
            this.proxyApiKeyRepository = proxyApiKeyRepository;
        }

        @Override
        public CompletableFuture<KeyAuthenticationToken> asyncLoad(String apiKey, Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                Mono<ProxyApiKey> byApiKey = proxyApiKeyRepository.findByApiKey(apiKey);

                // block 은 비동기로 진행되어야 하는 특별한 이유가 있는게 아니면 가능하면 쓰지 말 것.
                // WebFlux 를 이용해 애써 비동기로 만든 내용이 퇴색이 되는 code.
               /* return byApiKey.doOnNext(data -> {
                    if(StringUtil.isEmpty(data.getApiKey())) {
                       throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "TEST");
                    }
                }).map(data -> new KeyAuthenticationToken(data.getApiKey(), data.getRole())).block();*/

                return byApiKey
                        .map(data -> new KeyAuthenticationToken(data.getApiKey(), data.getApiKey(), data))
                       .block()
                        ;
            }, executor);
        }

        @Override
        public CompletableFuture<Map<String, KeyAuthenticationToken>> asyncLoadAll(Iterable<? extends String> keys, Executor executor) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CompletableFuture<KeyAuthenticationToken> asyncReload(String key, KeyAuthenticationToken oldValue, Executor executor) {
            return asyncLoad(key, executor);
        }
    }
}