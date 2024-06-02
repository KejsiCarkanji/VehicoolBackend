package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.Contract;
import com.vehicool.vehicool.persistence.entity.Lender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LenderRepository extends JpaRepository<Lender,Long> , QuerydslPredicateExecutor<Lender> {
    @Query("SELECT con from Contract con where con.lender.id = :id and con.lender.status.id = :statusId")
    List<Contract> contractRequests(Long id, Long statusId);

    @Query("SELECT avg(lr.rating) from LenderReview lr  where lr.lender.id =:id")
    float lenderRatingAvergage(Long id);
}
