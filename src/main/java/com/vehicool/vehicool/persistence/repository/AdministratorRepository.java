package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator,Long> {


}
