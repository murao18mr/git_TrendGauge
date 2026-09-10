package com.trendgauge.repository;

import com.trendgauge.model.entity.TargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TargetRepository extends JpaRepository<TargetEntity, Long> {
    Optional<TargetEntity> findByStoreIdAndTargetMonth(Long storeId, String targetManth);
}
