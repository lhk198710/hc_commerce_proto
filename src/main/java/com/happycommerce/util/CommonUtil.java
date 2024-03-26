package com.happycommerce.util;

import org.springframework.web.reactive.function.server.ServerRequest;

public class CommonUtil {
    public static String getTraceId(ServerRequest request){
        return (String) request.attribute("traceId").orElse("");
    }
}
