package com.vehicool.vehicool.business.querydsl;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.vehicool.vehicool.persistence.entity.QRenter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RenterQueryDsl implements QueryDsl<RenterFilter> {

    QRenter qRenter = QRenter.renter;

    @Override
    public Predicate filter(RenterFilter filter) {
        BooleanBuilder query = new BooleanBuilder();



        if (StringUtils.hasText(filter.getFirstName())) {
            query.and(qRenter.user.firstname.containsIgnoreCase(filter.getFirstName()));
        }
        if (StringUtils.hasText(filter.getLastName())) {
            query.and(qRenter.user.lastname.containsIgnoreCase(filter.getLastName()));
        }
        if (StringUtils.hasText(filter.getEmail())) {
            query.and(qRenter.user.email.containsIgnoreCase(filter.getEmail()));
        }
        if (StringUtils.hasText(filter.getPhoneNumber())) {
            query.and(qRenter.user.phoneNumber.containsIgnoreCase(filter.getPhoneNumber()));
        }

        return query;
    }
}
