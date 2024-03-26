package com.happycommerce.interfaces;

import com.happycommerce.dto.TestResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*@SecurityScheme(name = "Api-Key", scheme = "abc", in = SecuritySchemeIn.HEADER, type = SecuritySchemeType.APIKEY)*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RouterOperations({
        @RouterOperation(
                method = RequestMethod.GET,
                operation =
                @Operation(
                        description = "Api-Key 헤더 인증 하는 API 테스트",
                        operationId = "NoneSecure",
                        tags = "Study",
                        /*requestBody =
                        @RequestBody(
                                description = "Api-Key 헤더 인증 하는 API 테스트",
                                required = true,
                                content = @Content(schema = @Schema(implementation = String.class,
                                        requiredProperties = {"name", "age", "club", "nationality"}))),*/
                        responses = {
                                @ApiResponse(
                                        responseCode = "200",
                                        description = "Api-Key 헤더 인증 하는 API 테스트 정상 응답",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = TestResponseDto.class))
                                        }),
                                @ApiResponse(
                                        responseCode = "400",
                                        description = "Api-Key 헤더 인증 하는 API 테스트 400 에러 응답",
                                        content = {
                                                @Content(
                                                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                                                        schema = @Schema(implementation = TestResponseDto.class))
                                        })
                        }))
})
public @interface SecureApiInfo {}