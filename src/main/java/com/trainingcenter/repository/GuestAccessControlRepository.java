package com.trainingcenter.repository;

import com.trainingcenter.entity.GuestAccessControl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestAccessControlRepository extends JpaRepository<GuestAccessControl, String> {
}
