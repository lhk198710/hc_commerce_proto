package com.happycommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger Open API Doc 정의
 */
@OpenAPIDefinition(
        info = @Info(title = "WebFlux Server v0.0.1", description = "WebFlux 학습을 목적으로 생성한 서버", version = "v0.0.1"),
        security = {
                @SecurityRequirement(name = "Api-Key"),
        }
)
@SecuritySchemes({
        @SecurityScheme(name = "Api-Key",
                type = SecuritySchemeType.APIKEY,
                description = "일부 API 사용시 HTTP Request Header에 입력되어야 하는 Api-Key 문자열(S9 발급)",
                in = SecuritySchemeIn.HEADER,
                paramName = "Api-Key"),
})
@Configuration
public class SpringDocConfiguration {
        /*@Bean
        public GroupedOpenApi proxyApi1() {
                String[] paths = {"/v1/global/**"};

                return GroupedOpenApi.builder()
                        .group("서버 1번 그룹")
                        .pathsToMatch(paths)
                        .build();
        }

        @Bean
        public GroupedOpenApi proxyApi2() {
                String[] paths = {"/v1/global2/**"};

                return GroupedOpenApi.builder()
                        .group("서버 2번 그룹")
                        .pathsToMatch(paths)
                        .build();
        }*/
}
