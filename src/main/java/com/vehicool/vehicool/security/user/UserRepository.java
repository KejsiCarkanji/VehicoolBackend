package com.vehicool.vehicool.security.user;


import com.vehicool.vehicool.persistence.entity.Renter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, QuerydslPredicateExecutor<User> {

  Optional<User> findByUsername(String username);
}