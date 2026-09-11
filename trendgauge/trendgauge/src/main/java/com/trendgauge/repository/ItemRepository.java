package com.trendgauge.repository;

import com.trendgauge.model.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    @Query("SELECT i " +
            "FROM ItemEntity i " +
            "JOIN SaleEntity s ON i.saleId = s.id " +
            "WHERE s.storeId = :storeId " +
            "AND s.saleDate BETWEEN :startDate AND :endDate")
    List<ItemEntity> findByStoreIdAndSaleDateBetween(
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
