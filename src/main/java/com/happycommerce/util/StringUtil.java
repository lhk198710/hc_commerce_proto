package com.happycommerce.util;

import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class StringUtil extends StringUtils {
    private static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);

    /**
     * <pre>
     *  입력 문자열이 null 이거나, 공백 문자들만 포함하고 있을 경우 true 반환, 아니면 false 반환한다.
     * </pre>
     *
     * @param inputStr 테스트를 위한 문자열
     * @return 문자열이 null 이거나, 공백으로 이루어진 빈 문자열인 경우 true 반환
     */
    public static boolean isEmpty(String inputStr) {
        return inputStr == null || inputStr.trim().isEmpty();
    }

    public static boolean contains(List<Map<String, String>> list, String key, String value) {
        for (Map<String, String> map : list) {
            if (map.get(key).equals(value)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <PRE>
     * 자바 기본 날짜/시간 클래스 객체를 인자로 받아, "yyyy-MM-dd HH:mm:ss.SSSZ" 형식의 날짜/시간 문자열을 반환한다.
     * <p>
     * 날짜 변환 예시 : 2021-09-27 15:37:57.062+0900 (+0900 값은 그리니치 표준시 보다 9시간 차이가 나는 서울 표준시간을 나타낸다.)
     * </PRE>
     *
     * @param localDateTime 자바 기본 날짜 클래스 객체 (java.util.Date)
     * @return 표준 날짜/시간 형식의 문자열
     * @throws IllegalArgumentException 인자가 null 인 경우, IllegalArgumentException 예외 발생
     */
    public static String convertDateToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("date argument is null");
        }

        return convertDateToString(localDateTime, DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * <PRE>
     * 자바 기본 날짜/시간 클래스 객체와 format 인자로 지정한 형식(format)을 입력 받아 날짜/시간을 문자열 형식으로 변환한 후 반환한다.
     * </PRE>
     *
     * @param localDateTime   java.util.Date
     * @param format 날짜/시간 형식 <a href=
     *               "https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html">Java
     *               7 SimpleDateFormat.html</a> 참조
     * @return date format string
     * @throws IllegalArgumentException date 혹은 format 인자에 null 이거나, 잘못된 인자가 전달된 경우 발생
     */
    public static String convertDateToString(LocalDateTime localDateTime, String format) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("date argument is null");
        }
        if (isEmpty(format)) {
            throw new IllegalArgumentException("format argument is null or empty");
        }

        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * <pre>
     *  입력 문자열이 Y 일 경우 true 반환, 아니면 false 반환한다.
     * </pre>
     *
     * @param inputStr 테스트를 위한 문자열
     * @return 문자열이 null 이 아니고, 'Y'일 경우 true 반환
     */
    public static boolean isYn(String inputStr) {
        return inputStr != null && "Y".equalsIgnoreCase(inputStr);

    }

    /**
     * <pre>
     * 문자열의 길이를 byte 단위로 제한하는 문자열 자르기 메소드.
     * 예를 들어, "가나다"라는 문자열을 입력하고, maxLenInBytes 인자를 4로 설정한 후 substrInBytes 메소드를 실행할 경우, "가나"라는 문자열을 반환한다.
     * </pre>
     *
     * @param text          원본 문자열
     * @param maxLenInBytes byte 단위의 최대 길이
     * @return byte 단위의 길이로 잘라낸 문자열
     */
    public static String substrInBytes(String text, int maxLenInBytes) {
        // 빈 문자열이 입력되거나, 입력 문자열의 길이가 허용되는 최대 길이보다 같거나 작은 경우, 원본 문자열 반환
        if (!StringUtils.hasLength(text) || text.getBytes().length <= maxLenInBytes) {
            return text;
        }

        if (maxLenInBytes <= 0) {
            throw new IllegalArgumentException("value of maxLenInBytes must be greater than zero");
        }

        int len = text.length();
        int bytesLength = 0;
        StringBuilder sb = new StringBuilder();
        for (int idx = 0; idx < len; idx++) {
            String s = text.substring(idx, idx + 1);
            bytesLength = bytesLength + s.getBytes().length;
            if (bytesLength <= maxLenInBytes) {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    /**
     * <pre>
     *   입력된 문자열이 빈 문자열인 경우, 기본 문자열(defaultStr)로 대체한 후 반환한다.
     *   빈 문자열이 아닌 경우, 원본 문자열을 반환한다.
     * </pre>
     *
     * @param inputValue   빈 문자열인지 검사할 대상 문자열
     * @param defaultValue inputValue 문자열이 빈 문자열인 경우, 대체 되는 문자열
     * @throws IllegalArgumentException defaultValue 인자가 null 이거나 빈 문자열인 경우, 예외 발생
     */
    public static String getDefault(String inputValue, String defaultValue) {
        if (isEmpty(defaultValue)) {
            throw new IllegalArgumentException("defaultValue is null or empty");
        }
        if (isEmpty(inputValue)) {
            return defaultValue;
        }
        return inputValue;
    }

    /**
     * <PRE>
     * 입력된 문자열이 숫자 (정수 혹은 실수)인지 확인한다.
     * </PRE>
     *
     * @param strNum 점검 대상 문자열
     * @return 숫자이면 true, 아니면 false
     */
    public static boolean isNumeric(String strNum) {
        if (isEmpty(strNum)) {
            return false;
        }

        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * <PRE>
     * Base64 방식으로 인코딩한 문자열을 반환한다.
     * </PRE>
     *
     * @param plainText Base64 방식으로 인코딩할 문자열
     * @return Base64 방식으로 인코딩된 문자열
     * @throws IllegalArgumentException 문자열이 입력될 경우, IllegalArgumentException 예외 발생
     */
    public static String encodeBase64(String plainText) {
        if (isEmpty(plainText)) {
            throw new IllegalArgumentException("plainText argument is null of empty");
        }
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(bytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    /**
     * <PRE>
     * Base64 방식으로 디코딩한 문자열을 반환한다.
     * </PRE>
     *
     * @param encodedText Base64 방식으로 디코딩할 문자열
     * @return Base64 방식으로 디코딩된 문자열
     * @throws IllegalArgumentException 문자열이 입력될 경우, IllegalArgumentException 예외 발생
     */
    public static String decodeBase64(String encodedText) {
        if (isEmpty(encodedText)) {
            throw new IllegalArgumentException("encodedText argument is null of empty");
        }
        byte[] bytes = encodedText.getBytes(StandardCharsets.UTF_8);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(bytes);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
