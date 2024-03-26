package com.happycommerce.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happycommerce.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = super.getErrorAttributes(request, options); // 기존 Map 활용
        log.info("Reactive DefaultErrorAttributes : {}", map);
        log.info("Reactive getError function result : ", getError(request));

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

        int status = (int) map.get("status");
        String message = status == HttpStatus.INTERNAL_SERVER_ERROR.value() ? "INTERNAL_ERROR" : throwable.getMessage();

        ErrorMessage errorMessage = new ErrorMessage(status
                , LocalDateTime.now()
                , message
                , (String) map.get("path"));

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(errorMessage, Map.class);
    }
}
