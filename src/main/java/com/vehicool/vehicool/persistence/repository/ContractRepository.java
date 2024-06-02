package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract,Long> {

}
