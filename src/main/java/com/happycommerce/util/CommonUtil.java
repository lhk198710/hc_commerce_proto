package com.happycommerce.util;

import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.Random;

public class CommonUtil {
    private static final Random RANDOM = new Random();
    public static String getTraceId(ServerRequest request){
        return (String) request.attribute("traceId").orElse("");
    }

    public static String generateLoginToken() {
        // 로그인 토큰 생성 함수
        String localDateTime = StringUtil.convertDateToString(LocalDateTime.now(), "yyyyMMddHHmmss");

        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));  // 0~9 사이의 숫자를 랜덤으로 가져옵니다.
        }

        return localDateTime + sb;
    }
}
