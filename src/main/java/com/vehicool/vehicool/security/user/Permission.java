package com.vehicool.vehicool.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),


    LENDER_READ("lender:read"),
    LENDER_UPDATE("lender:update"),
    LENDER_CREATE("lender:create"),
    LENDER_DELETE("lender:delete"),

   RENTER_READ("renter:read"),
    RENTER_UPDATE("renter:update"),
    RENTER_CREATE("renter:create"),
    RENTER_DELETE("renter:delete")

    ;

    @Getter
    private final String permission;
}
