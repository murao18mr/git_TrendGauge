package com.trendgauge.repository;

import com.trendgauge.model.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    Optional<ReportEntity> findBySaleId(Long saleId);
}
