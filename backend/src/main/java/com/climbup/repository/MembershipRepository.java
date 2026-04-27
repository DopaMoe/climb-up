package com.climbup.repository;

import com.climbup.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByUserId(Long userId);

    @Query("SELECT m FROM Membership m WHERE m.user.id = :userId AND m.status = 'ACTIVE'")
    List<Membership> findActiveMembershipsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'ACTIVE'")
    long countActive();

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'PENDING'")
    long countPending();
}
