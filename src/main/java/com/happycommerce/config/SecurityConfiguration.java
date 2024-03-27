package com.happycommerce.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.happycommerce.ErrorMessage;
import com.happycommerce.dto.ClientResponseDto;
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

                .exceptionHandling().authenticationEntryPoint(serverAuthenticationEntryPoint()) // 401 Error(인증에러)
                .and()

                .exceptionHandling().accessDeniedHandler(serverAccessDeniedHandler()) // 403 Error(인가에러)
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
     *
     * @return ServerAuthenticationEntryPoint
     */
    private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, authEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.error("Unauthorized error: {}, Requested path : {}", authEx.getMessage(), requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);

            ClientResponseDto clientResponseDto = new ClientResponseDto();
            clientResponseDto.setStatus(HttpStatus.UNAUTHORIZED.value());
            clientResponseDto.setData(authEx.getMessage());

            return serverHttpResponse(serverHttpResponse, clientResponseDto);
        };
    }

    /**
     * Http status 403 error response
     *
     * @return ServerAccessDeniedHandler
     */
    private ServerAccessDeniedHandler serverAccessDeniedHandler() {
        return (exchange, accessEx) -> {
            String requestPath = exchange.getRequest().getPath().value();

            log.error("AccessDenied error: {}, Requested path : {}", accessEx.getMessage(), requestPath);

            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            serverHttpResponse.setStatusCode(HttpStatus.FORBIDDEN);

            ClientResponseDto clientResponseDto = new ClientResponseDto();
            clientResponseDto.setStatus(HttpStatus.FORBIDDEN.value());
            clientResponseDto.setData(accessEx.getMessage());

            return serverHttpResponse(serverHttpResponse, clientResponseDto);
        };
    }

    private <T> Mono<Void> serverHttpResponse(ServerHttpResponse serverHttpResponse, T clientResponseDto) {
        try {
            byte[] errorByte = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .writeValueAsBytes(clientResponseDto);
            DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
            return serverHttpResponse.writeWith(Mono.just(dataBuffer));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return serverHttpResponse.setComplete();
        }
    }
}