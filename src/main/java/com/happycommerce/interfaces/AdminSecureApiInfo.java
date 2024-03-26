package com.happycommerce.interfaces;

import com.happycommerce.ErrorMessage;
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
                        description = "Api-Key 헤더 인증 하는 API 테스트(어드민 권한[abc])",
                        operationId = "NoneSecure",
                        tags = "Study",
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
                                                        schema = @Schema(implementation = ErrorMessage.class))
                                        })
                        }))
})
public @interface AdminSecureApiInfo {}