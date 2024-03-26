package com.happycommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class HPCAuthRequestDto {
    @NotNull(message = "rcgnKey is not null.")
    @Valid
    @Schema(name = "rcgnKey", required = true, description = "rcgnKey")
    @Builder.Default
    private String rcgnKey = "dcedc61f0659b16716f325205e26b7fdb71768524ef14111eb731738e5bd1e91";

    @NotNull(message = "instCd is not null.")
    @Valid
    @Schema(name = "instCd", required = true, description = "instCd")
    @Builder.Default
    private String instCd = "OSCO";

    @NotNull(message = "tlgmNo is not null.")
    @Valid
    @Schema(name = "tlgmNo", required = true, description = "tlgmNo")
    @Builder.Default
    private String tlgmNo = "2000";

    @NotNull(message = "tlgmChnlCd is not null.")
    @Valid
    @Schema(name = "tlgmChnlCd", required = true, description = "tlgmChnlCd")
    @Builder.Default
    private String tlgmChnlCd = "X0";

    @NotNull(message = "trsDt is not null.")
    @Valid
    @Schema(name = "trsDt", required = true, description = "전문 전송 년월일(YYYYMMDD)")
    private String trsDt;

    @NotNull(message = "trsTm is not null.")
    @Valid
    @Schema(name = "trsTm", required = true, description = "전문 전송 시분초(HHIISS)")
    private String trsTm;

    @NotNull(message = "tracNo is not null.")
    @Valid
    @Schema(name = "tracNo", required = true, description = "전문 추적 번호")
    private String tracNo;

    @NotNull(message = "tlgmClCd is not null.")
    @Valid
    @Schema(name = "tlgmClCd", required = true, description = "tlgmClCd")
    @Builder.Default
    private String tlgmClCd = "ON";

    @NotNull(message = "reqClCd is not null.")
    @Valid
    @Schema(name = "reqClCd", required = true, description = "reqClCd")
    @Builder.Default
    private String reqClCd = "10";

    @NotNull(message = "reqBrndCd is not null.")
    @Valid
    @Schema(name = "reqBrndCd", required = true, description = "reqBrndCd")
    @Builder.Default
    private String reqBrndCd = "A007";

    @NotNull(message = "reqChnlCd is not null.")
    @Valid
    @Schema(name = "reqChnlCd", required = true, description = "reqChnlCd")
    @Builder.Default
    private String reqChnlCd = "20";

    @NotNull(message = "reqUserId is not null.")
    @Valid
    @Schema(name = "reqUserId", required = true, description = "HPC 회원 아이디")
    private String reqUserId;

    @NotNull(message = "mbrAuthMthdCd is not null.")
    @Valid
    @Schema(name = "mbrAuthMthdCd", required = true, description = "mbrAuthMthdCd")
    @Builder.Default
    private String mbrAuthMthdCd = "10";

    @NotNull(message = "onlnId is not null.")
    @Valid
    @Schema(name = "onlnId", required = true, description = "HPC 회원 아이디")
    private String onlnId;

    @NotNull(message = "onlnPwd is not null.")
    @Valid
    @Schema(name = "onlnPwd", required = true, description = "HPC 회원 비밀번호")
    private String onlnPwd;

    @NotNull(message = "mbrIdfNo is not null.")
    @Valid
    @Schema(name = "mbrIdfNo", required = true, description = "mbrIdfNo")
    private String mbrIdfNo;
}
