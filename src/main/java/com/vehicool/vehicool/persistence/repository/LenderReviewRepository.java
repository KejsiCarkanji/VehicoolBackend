package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.LenderReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LenderReviewRepository extends JpaRepository<LenderReview,Long> {

}
