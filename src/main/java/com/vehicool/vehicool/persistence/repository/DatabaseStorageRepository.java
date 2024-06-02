package com.vehicool.vehicool.persistence.repository;


import com.vehicool.vehicool.persistence.entity.ConfidentialFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatabaseStorageRepository extends JpaRepository<ConfidentialFile, Long> {


    Optional<ConfidentialFile> findByName(String fileName);
}
