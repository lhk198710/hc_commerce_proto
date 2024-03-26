package com.happycommerce.security;

import com.happycommerce.entity.ProxyApiKey;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * SpringSecurity 인증 데이터 관리 토큰
 */
@ToString
@Slf4j
public class KeyAuthenticationToken implements Authentication {
    private final String apiKey; // 유저 식별 요소. 별도로 정해진 것은 없어서 ApiKey 지정
    private final String principal; // 유저 식별 요소. 별도로 정해진 것은 없어서 ApiKey 지정
    private boolean authenticated = false;
    private final ProxyApiKey proxyApiKey;

    public KeyAuthenticationToken(final String apiKey, final String principal, final ProxyApiKey proxyApiKey) {
        this.apiKey = apiKey;
        this.principal = principal;
        this.proxyApiKey = proxyApiKey;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> proxyApiKey.getRole());

        return collection; // 권한 관리 할 생각 없을 경우 null로 return & SecurityConfiguraion의 hasAnyRole등 권한 지정 부분 삭제 필요
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return principal;
    }
}