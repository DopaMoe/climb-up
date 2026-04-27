package com.climbup.repository;

import com.climbup.model.GlobalConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalConfigRepository extends JpaRepository<GlobalConfig, Long> {
}
