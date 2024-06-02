package com.vehicool.vehicool.business.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.security.user.QUser;
import org.springframework.stereotype.Component;

@Component
public class UserQueryDsl implements QueryDsl<UserFilter>{
    QUser qUser = QUser.user;

    @Override
    public Predicate filter(UserFilter filter) {
        BooleanBuilder query = new BooleanBuilder();

        if (filter.getUserStatusId()!=null) {
            query.and(QUser.user.userStatus.id.eq(filter.getUserStatusId()));
        }
        if (filter.getLenderStatusId()!=null) {
            query.and(QUser.user.lenderProfile.status.id.eq(filter.getLenderStatusId()));
        }
        if (filter.getRenterStatusId()!=null) {
            query.and(QUser.user.renterProfile.status.id.eq(filter.getRenterStatusId()));
        }
        return query;
    }
}
