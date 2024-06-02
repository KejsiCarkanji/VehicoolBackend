package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.DataPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataPoolRepository extends JpaRepository<DataPool,Long> {
    List<DataPool> findAllByEnumName(String enumName);
    DataPool findByEnumLabel(String enumLabel);
}
