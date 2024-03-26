package com.happycommerce.entity;

import com.happycommerce.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table(name = "T_PROXY_API_KEY")
@Getter
@Setter
@Builder
@ToString
public class ProxyApiKey {
    @Id
    @Column(value = "SEQ")
    private Long seq;
    @Column(value = "CLIENT_NAME")
    private String clientName;
    @Column(value = "CLIENT_CODE")
    private String clientCode;
    @Column(value = "USE_YN")
    private String useYn;
    @Column(value = "API_KEY")
    private String apiKey;
    @Column(value = "ROLE")
    private String role;
    @Column(value = "REG_DATE")
    @CreatedDate
    private LocalDateTime regDate;
    @Column(value = "REG_ID")
    private String regId;
    @Column(value = "UPD_DATE")
    @LastModifiedDate
    private LocalDateTime updDate;
    @Column(value = "UPD_ID")
    private String updId;
}
