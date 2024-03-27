package com.happycommerce.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happycommerce.ErrorMessage;
import com.happycommerce.dto.ClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    final String DEFAULT_ERROR_CODE = "9999";
    final String DEFAULT_ERROR_MSG = "SYSTEM_ERROR";

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options); // 기존 Map 활용
        log.info("Reactive DefaultErrorAttributes map:{}, getError", map, getError(request));

        // CustomException 존재 시 활용
        /*
        Throwable throwable = getError(request);
        if(throwable instanceof CustomException) {
            CustomException ex = (CustomException) getError(request);
            map.put("exception", ex.getClass().getSimpleName());
            map.put("message", ex.getErrorCode().getMessage());
            map.put("status", ex.getErrorCode().getStatus());
            map.put("errorCode", ex.getErrorCode().getCode());
        }*/

        // Default error object by hk.lee
        Throwable throwable = getError(request);

        if(throwable instanceof CommonApiException) {
            return makeResponseErrorObject(map, request);
        } else {
            // Default Exception
            return makeClientResponseDto(map, DEFAULT_ERROR_CODE, DEFAULT_ERROR_MSG);
        }
    }

    /**
     * 예외 객체 별 응답 정보 생성 함수
     * @param map Map<String, Object> map
     * @param request ServerRequest
     * @return Map
     * @param <T>
     */
    public <T> Map makeResponseErrorObject(Map<String, Object> map, ServerRequest request) {
        Throwable throwable = getError(request);
        String errorCode;
        String errorMessage;

        if(throwable instanceof CommonApiException) {
            map.put("status", HttpStatus.UNAUTHORIZED.value());
            CommonApiException exObj = (CommonApiException) getError(request);
            errorCode = exObj.getErrorCode();
            errorMessage = exObj.getErrorMessage();
        } else {
            // Default Exception
            int originStatus = (int) map.get("status");
            map.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

            errorCode = originStatus == 500 ? DEFAULT_ERROR_CODE : String.valueOf(originStatus);
            errorMessage = originStatus == 500 ? DEFAULT_ERROR_MSG : throwable.getMessage();
        }

        return makeClientResponseDto(map, errorCode, errorMessage);
    }

    private <T> Map makeClientResponseDto(Map<String, Object> map, String errorCode, String errorMessage) {
        ClientResponseDto clientResponseDto = ClientResponseDto.builder()
                .status((int) map.get("status"))
                .data(new HashMap() {
                    {
                        put("errorCode", errorCode); // Exception 에러코드
                        put("errorMessage", errorMessage); // Exception 에러 메시지
                    }
                })
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(clientResponseDto, Map.class);
    }
}
