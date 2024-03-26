package com.happycommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthRequestDto {
    @NotNull(message = "hpcId is not null.")
    @Valid
    @Schema(name = "hpcId", required = true, description = "해피포인트 회원 아이디")
    private String hpcId;
    @NotNull(message = "hpcPwd is not null.")
    @Valid
    @Schema(name = "hpcPwd", required = true, description = "해피포인트 회원 비밀번호")
    private String hpcPwd;
}
