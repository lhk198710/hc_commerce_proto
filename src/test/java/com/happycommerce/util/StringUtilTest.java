package com.happycommerce.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {
    @Test
    void isEmpty() {
        String str = null;
        Assertions.assertEquals(StringUtil.isEmpty(str), true);
    }

    @Test
    void contains() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        map.put("Test Title 1", "Test Value 1");
        map.put("Test Title 2", "Test Value 2");
        map.put("Test Title 3", "Test Value 3");

        list.add(map);

        Assertions.assertEquals(StringUtil.contains(list, "Test Title 3", "Test Value 3"), true);
    }

    @Test
    void convertDateToString() {
    }

    @Test
    void testConvertDateToString() {
    }

    @Test
    void isYn() {
    }

    @Test
    void substrInBytes() {
    }

    @Test
    void getDefault() {
    }

    @Test
    void isNumeric() {
    }

    @Test
    void encodeBase64() {
    }

    @Test
    void decodeBase64() {
    }
}