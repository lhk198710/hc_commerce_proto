package com.happycommerce.enums;

public enum Role {
    ROLE_ADMIN("ROLE_ADMIN", "어드민"),
    ROLE_MEMBER("ROLE_MEMBER", "일반");

    private String roleName;
    private String roleDescription;

    Role(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
    }

    public String getRoleName() {
        return this.roleName;
    }
}
