package com.climbup.repository;

import com.climbup.model.CheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Page<CheckIn> findByUserIdOrderByCheckedInAtDesc(Long userId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.checkedInAt >= :since")
    long countSince(@Param("since") LocalDateTime since);

    @Query("SELECT c FROM CheckIn c ORDER BY c.checkedInAt DESC")
    List<CheckIn> findRecent(Pageable pageable);
}
