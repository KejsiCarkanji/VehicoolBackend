package com.vehicool.vehicool.persistence.repository;

import com.vehicool.vehicool.persistence.entity.BannedUsersAppealing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannedUsersAppealingRepository extends JpaRepository<BannedUsersAppealing,Long> {

}
