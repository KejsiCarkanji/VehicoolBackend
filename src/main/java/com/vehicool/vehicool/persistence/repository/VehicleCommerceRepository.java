package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.Vehicle;
import com.vehicool.vehicool.persistence.entity.VehicleCommerce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleCommerceRepository extends JpaRepository<VehicleCommerce,Long> {

}
