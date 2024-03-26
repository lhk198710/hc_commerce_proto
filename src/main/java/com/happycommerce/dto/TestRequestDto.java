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
public class TestRequestDto {
    @NotNull(message = "clientName is not null.")
    @Valid
    @Schema(name = "clientName", required = true, description = "클라이언트명")
    private String clientName;
    @NotNull(message = "clientCode is not null.")
    @Valid
    @Schema(name = "clientCode", required = true, description = "클라이언코드")
    private String clientCode;
    @Schema(name = "useYn", defaultValue = "Y", required = true, description = "사용 여부(Y)")
    @Pattern(regexp = "^(Y|N)$", message = "Value must be Y or N")
    @NotNull(message = "useYn is not null.")
    @Valid
    private String useYn = "Y";
    @NotNull(message = "apiKey is not null.")
    @Valid
    @Schema(name = "apiKey", required = true, description = "API Key")
    private String apiKey;
    @NotNull(message = "role is not null.")
    @Valid
    @Pattern(regexp = "^(ROLE_ADMIN|ROLE_MEMBER)$", message = "Value must be ROLE_MEMBER or ROLE_ADMIN")
    @Schema(name = "role", defaultValue = "ROLE_MEMBER", required = true, description = "권한(ROLE_ADMIN, ROLE_MEMBER)")
    private String role; // Enum 고려
    @Schema(name = "regId", required = true, description = "등록자 아이디")
    private String regId;
    @Schema(name = "updId", required = true, description = "등록자 아이디")
    private String updId;
}
