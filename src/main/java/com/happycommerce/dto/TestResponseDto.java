package com.happycommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * API 응답값 테스트 모델
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class TestResponseDto {
    @Schema(name = "message", required = true, description = "메시지")
    private String message;
}
