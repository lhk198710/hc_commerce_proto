package com.happycommerce.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonUtil {

    /**
     * Object를 Json으로 변경
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        ObjectMapper om = new ObjectMapper();
        try {
            return  om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * json을 List<Object> 로 변경
     *
     * @param jsonStr
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> convertJsonToObjList(String jsonStr, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<T> list = null;

        try {
            list = mapper.readValue(jsonStr, mapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return list;
    }
}
