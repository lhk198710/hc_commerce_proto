package com.happycommerce.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Spring security filter 수행 방식 정의
 * - 실제 인증은 진행 안 함
 * - 필터에서 인증 객체를 받아 AuthenticationProvider에 위임하는 역할
 */
@Slf4j
@Component
public class KeyAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.fromSupplier(() -> {
            if (authentication != null && authentication.getCredentials() != null) {
                authentication.setAuthenticated(true);
            }

            return authentication;
        });
    }
}