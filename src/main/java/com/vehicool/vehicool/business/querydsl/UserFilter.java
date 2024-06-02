package com.vehicool.vehicool.business.querydsl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilter {
    private Long userStatusId;
    private Long lenderStatusId;
    private Long renterStatusId;
}
