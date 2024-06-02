package com.vehicool.vehicool.business.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.persistence.entity.QLender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LenderQueryDsl implements QueryDsl<LenderFilter> {

    QLender qLender = QLender.lender;

    @Override
    public Predicate filter(LenderFilter filter) {
        BooleanBuilder query = new BooleanBuilder();



        if (StringUtils.hasText(filter.getFirstName())) {
            query.and(qLender.user.firstname.containsIgnoreCase(filter.getFirstName()));
        }
        if (StringUtils.hasText(filter.getLastName())) {
            query.and(qLender.user.lastname.containsIgnoreCase(filter.getLastName()));
        }
        if (StringUtils.hasText(filter.getEmail())) {
            query.and(qLender.user.email.containsIgnoreCase(filter.getEmail()));
        }
        if (StringUtils.hasText(filter.getPhoneNumber())) {
            query.and(qLender.user.phoneNumber.containsIgnoreCase(filter.getPhoneNumber()));
        }

        return query;
    }
}
