package com.vehicool.vehicool.business.querydsl;

import com.querydsl.core.types.Predicate;

public interface QueryDsl<K> {
    Predicate filter(K filter);
}