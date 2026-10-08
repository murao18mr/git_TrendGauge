package com.trendgauge.repository;

import com.trendgauge.model.entity.IntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, Long> {
    Optional<IntegrationEntity> findByStoreId(Long storeId);

    List<IntegrationEntity> findByAutoSyncTrue();
}
