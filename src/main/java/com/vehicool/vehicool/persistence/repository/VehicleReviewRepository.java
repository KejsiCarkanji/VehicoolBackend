package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.VehicleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleReviewRepository extends JpaRepository<VehicleReview,Long> {
}
