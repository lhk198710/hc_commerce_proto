package com.happycommerce.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 테스트 목적으로 만든 임시 에러 response dto
 * - 정식으로 프로젝트 들어가면 형식 정식으로 지정 & 사용 필요
 */
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDto {
    @Schema(name = "status", example = "200", required = true, description = "HTTP 응답코드")
    private int status;
    @Schema(name = "data", example = "data", description = "client 에 응답되는 실 데이터 객체")
    private Object data;
}

