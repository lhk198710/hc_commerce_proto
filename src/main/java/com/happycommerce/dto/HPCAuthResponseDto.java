package com.happycommerce.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HPCAuthResponseDto {
    @Schema(name = "rcgnKey", required = true, description = "rcgnKey")
    private String rcgnKey;

    @Schema(name = "instCd", required = true, description = "instCd")
    private String instCd;

    @Schema(name = "tlgmNo", required = true, description = "tlgmNo")
    private String tlgmNo;

    @Schema(name = "tlgmChnlCd", required = true, description = "tlgmChnlCd")
    private String tlgmChnlCd;

    @Schema(name = "trsDt", required = true, description = "전문 전송 년월일(YYYYMMDD)")
    private String trsDt;

    @Schema(name = "trsTm", required = true, description = "전문 전송 시분초(HHIISS)")
    private String trsTm;

    @Schema(name = "tracNo", required = true, description = "전문 추적 번호")
    private String tracNo;

    @Schema(name = "tlgmClCd", required = true, description = "tlgmClCd")
    private String tlgmClCd;

    @Schema(name = "rpsCd", description = "rpsCd")
    private String rpsCd;

    @Schema(name = "rpsMsgCtt", description = "rpsMsgCtt")
    private String rpsMsgCtt;

    @Schema(name = "reqClCd", required = true, description = "reqClCd")
    private String reqClCd;

    @Schema(name = "reqBrndCd", required = true, description = "reqBrndCd")
    private String reqBrndCd;

    @Schema(name = "reqChnlCd", required = true, description = "reqChnlCd")
    private String reqChnlCd;

    @Schema(name = "mbrAuthMthdCd", required = true, description = "mbrAuthMthdCd")
    private String mbrAuthMthdCd;

    @Schema(name = "onlnId", required = true, description = "HPC 회원 아이디")
    private String onlnId;

    @Schema(name = "onlnPwd", required = true, description = "HPC 회원 비밀번호")
    private String onlnPwd;

    @Schema(name = "mbrIdfNo", required = true, description = "mbrIdfNo")
    private String mbrIdfNo;

    @Schema(name = "mbrNm",  description = "mbrNm")
    private String mbrNm;
    @Schema(name = "birth",  description = "birth")
    private String birth;
    @Schema(name = "genClcd",  description = "genClcd")
    private String genClcd;
    @Schema(name = "lcalRsdtYn",  description = "lcalRsdtYn")
    private String lcalRsdtYn;
    @Schema(name = "hpNo",  description = "hpNo")
    private String hpNo;
    @Schema(name = "cardNo",  description = "cardNo")
    private String cardNo;
    @Schema(name = "mbrNo",  description = "mbrNo")
    private String mbrNo;
    @Schema(name = "cardStCd",  description = "cardStCd")
    private String cardStCd;
    @Schema(name = "cardProdCatgCd",  description = "cardProdCatgCd")
    private String cardProdCatgCd;
    @Schema(name = "cardProdCd",  description = "cardProdCd")
    private String cardProdCd;
    @Schema(name = "mbrGrCd",  description = "mbrGrCd")
    private String mbrGrCd;
    @Schema(name = "mbrGrCdNm",  description = "mbrGrCdNm")
    private String mbrGrCdNm;
    @Schema(name = "remPt",  description = "remPt")
    private String remPt;
    @Schema(name = "rpsDtlCd",  description = "rpsDtlCd")
    private String rpsDtlCd;
    @Schema(name = "rpsDtlMsg",  description = "rpsDtlMsg")
    private String rpsDtlMsg;

    public String hpcAuthResponseOf() throws JsonProcessingException {
        Map<String, String> resultMap = new HashMap<>();

        resultMap.put("hpcResultCode", this.rpsDtlCd);
        resultMap.put("hpcResultMessage", this.rpsDtlMsg);
        resultMap.put("mbrNo", this.mbrNo);
        resultMap.put("cardNo", this.cardNo);

        return new ObjectMapper().writeValueAsString(resultMap);
    }
}
