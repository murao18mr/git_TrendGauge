package com.trendgauge.repository;

import com.trendgauge.model.entity.IntegrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationRepository extends JpaRepository<IntegrationEntity, Long> {
}
