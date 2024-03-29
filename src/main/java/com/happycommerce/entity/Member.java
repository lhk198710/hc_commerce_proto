package com.happycommerce.entity;


import com.happycommerce.util.StringUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "T_MEMBER")
@Getter
@Setter
@Builder
@ToString
public class Member {
    //public class Member implements Persistable {
    @Id
    @Column(value = "SEQ")
    private Long seq;
    @Column(value = "MBRNO")
    private String mbrNo;
    @Column(value = "NAME")
    private String name;
    @Column(value = "ID")
    private String id;
    @Column(value = "HPC_CARD_NO")
    private String hpcCardNo;
    @Column(value = "HPC_MBR_STATUS")
    private String hpcMbrStatus;
    @Column(value = "SHOPBY_MBR_STATUS")
    private String shopbyMbrStatus;
    @Column(value = "LOGIN_TOKEN")
    private String loginToken;
    @Column(value = "ACCESS_TOKEN")
    private String accessToken;
    @Column(value = "ACCESS_TOKEN_EXPIRED_DATE")
    private LocalDateTime accessTokenExpiredDate;
    @Column(value = "REG_DATE")
    @CreatedDate
    private LocalDateTime regDate;
    @Column(value = "REG_ID")
    @Builder.Default
    private String regId = "SYSTEM";
    @Column(value = "UPD_DATE")
    @LastModifiedDate
    private LocalDateTime updDate;
    @Column(value = "UPD_ID")
    @Builder.Default
    private String updId = "SYSTEM";
/*
    @Override
    public boolean isNew() {
        //return StringUtil.isEmpty(this.mbrNo);
        return !(seq == null || seq == 0)
    }*/
}

