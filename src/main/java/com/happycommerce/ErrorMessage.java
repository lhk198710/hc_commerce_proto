package com.happycommerce;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 테스트 목적으로 만든 임시 에러 response dto
 * - 정식으로 프로젝트 들어가면 형식 정식으로 지정 & 사용 필요
 */
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    @Schema(name = "status", example = "200", required = true, description = "HTTP 응답코드")
    private int status;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Schema(name = "localDateTime")
    private LocalDateTime localDateTime;
    @Schema(name = "errorMessage", example = "errorMessage", required = true, description = "에러 메시지")
    private String errorMessage;
    @Schema(name = "path", required = false, description = "Client 요청 Endpoint")
    private String path;
}
