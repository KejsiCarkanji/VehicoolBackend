package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.RenterReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RenterReviewRepository extends JpaRepository<RenterReview,Long> {
    @Query("SELECT avg(rr.rating) from RenterReview rr  where rr.renter.id =:id")
    float renterRatingAvergage(Long id);
}
