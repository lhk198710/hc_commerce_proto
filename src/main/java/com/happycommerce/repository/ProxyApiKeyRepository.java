package com.happycommerce.repository;

import com.happycommerce.entity.ProxyApiKey;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ProxyApiKeyRepository extends R2dbcRepository<ProxyApiKey, Long> {
    @Query("SELECT * FROM t_proxy_api_key WHERE api_key=:apiKey")
    Mono<ProxyApiKey> findByApiKey(String apiKey);

    @Query("DELETE FROM t_proxy_api_key WHERE api_key=:apiKey")
    Mono<Void> deleteByApiKey(String apiKey);
}
