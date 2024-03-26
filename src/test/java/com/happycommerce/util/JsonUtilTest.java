package com.happycommerce.util;

import com.happycommerce.ErrorMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JsonUtilTest {
    @Test
    public void toJson() {
        ErrorMessage errorMessage = new ErrorMessage(200, LocalDateTime.now(), "JsonObject ErrMsg", "JsonObject path");
        String result = JsonUtil.toJson(errorMessage);

        Assertions.assertNotNull(result);
    }
}

