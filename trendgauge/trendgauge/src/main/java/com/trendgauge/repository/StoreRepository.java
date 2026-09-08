package com.trendgauge.repository;

import com.trendgauge.model.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findByStoreCode(String storeCode);
}
