package com.climbup.repository;

import com.climbup.model.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipTypeRepository extends JpaRepository<MembershipType, Long> {
    List<MembershipType> findByIsActiveTrue();
}
