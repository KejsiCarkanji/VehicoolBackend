package com.vehicool.vehicool.security.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.vehicool.vehicool.security.user.Permission.*;


@RequiredArgsConstructor
public enum Role {
    BANNED_USER(Collections.emptySet()),
    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE
                    )
    ),
    LENDER(
            Set.of(
                    LENDER_READ,
                    LENDER_UPDATE,
                    LENDER_DELETE,
                    LENDER_CREATE
            )
    ),
    RENTER(
            Set.of(
                    LENDER_READ,
                    LENDER_UPDATE,
                    LENDER_DELETE,
                    LENDER_CREATE
            )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}