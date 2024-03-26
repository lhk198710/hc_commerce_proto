package com.happycommerce.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.happycommerce.ErrorMessage;
import com.happycommerce.security.KeyAuthenticationConverter;
import com.happycommerce.security.KeyAuthenticationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {
    // API Key 인증 무시 경로
    private final String[] ignoreApiKeyAuthPaths = {
            "/error", "/favicon.ico",
            "/api/v1/nonsecure**", // Api-Key header 인증 없는 Test API
            "/swagger-ui/**", "/swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**", "/webjars/**" // Swagger 관련
    };

    @Bean
    @DependsOn({"methodSecurityExpressionHandler"})
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         KeyAuthenticationManager keyAuthenticationManager,
                                                         KeyAuthenticationConverter keyAuthenticationConverter) {
        final AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(keyAuthenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(keyAuthenticationConverter);

        return http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()

                // 인증 에러(APIKey)
                /*.exceptionHandling().authenticationEntryPoint((exchange, denied) -> {
                    log.error("Http Status 401 error. {}", exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
                    return Mono.fromRunnable(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    }); // 테스트 위해 임시로 작성.
                })*/
                .exceptionHandling().authenticationEntryPoint(serverAuthenticationEntryPoint()) // 401 Error
                .and()
                // 인가 에러(ROLE)
                /*.exceptionHandling().accessDeniedHandler((exchange, denied) -> {
                    log.error("Http Status 403 error. {}", exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
                    return Mono.fromRunnable(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    }); // 테스트 위해 임시로 작성
                })*/
                .exceptionHandling().accessDeniedHandler(serverAccessDeniedHandler()) // 403 Error
                .and()

                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // SESSION STATELESS
                .authorizeExchange()
                .pathMatchers(ignoreApiKeyAuthPaths) // 인증 거치지 않는 URL Pattern 기입. 나머지는 모두 인증 처리 필요.
                .permitAll()

                // API-Key 필터링 권한 설정 부분. ROLL_ADMIN, ROLE_MEMBER. 자제적으로 ROLE_프리픽스가 붙어있는 관계로 'ROLE_' 생략 가능.
                .pathMatchers("/api/admin/v1/**").hasAnyRole("ADMIN")

                .anyExchange().authenticated()
                .and()
                
                // 나머지 API에 대해서는 Api-Key Header check 진행
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Http status 401 error response
     * @return ServerAuthenticationEntryPoint
     */
    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, authEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.error("Unauthorized error: {}, Requested path : {}", authEx.getMessage(), requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);

            ErrorMessage errorMessage = new ErrorMessage(HttpStatus.UNAUTHORIZED.value()
                    , LocalDateTime.now()
                    , authEx.getMessage()
                    , requestPath); // Test 목적으로 만든 인증 실패 에러 객체. 나중에 응답 객체 형식이 정해지면 해당 객체를 사용해야함.

            return serverHttpResponse(serverHttpResponse, errorMessage);
        };
    }

    /**
     * Http status 403 error response
     * @return ServerAccessDeniedHandler
     */
    private ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, accessEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.error("AccessDenied error: {}, Requested path : {}", accessEx.getMessage(), requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);

            ErrorMessage errorMessage = new ErrorMessage(HttpStatus.FORBIDDEN.value()
                    , LocalDateTime.now()
                    , accessEx.getMessage()
                    , requestPath);

            return serverHttpResponse(serverHttpResponse, errorMessage);
        };
    }

    private Mono<Void> serverHttpResponse(ServerHttpResponse serverHttpResponse, ErrorMessage errorMessage) {
        try {
            byte[] errorByte = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsBytes(errorMessage);
            DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
            return serverHttpResponse.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return serverHttpResponse.setComplete();
        }
    }
}