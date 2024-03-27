package com.happycommerce.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 일반적인 에러 성공 케이스가 아닌 경우 생성시키는 예외 객체
 *
 * <p>errorCode : Client 에게 응답하는 HPC 에러 코드</p>
 * <p>errorMessage : Client 에게 응답하는 HPC 에러 메시지</p>
 *
 * @author hk.lee
 */
@Getter
@Setter
public class CommonApiException extends RuntimeException {
    private String errorCode;
    private String errorMessage = "";

    public CommonApiException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}