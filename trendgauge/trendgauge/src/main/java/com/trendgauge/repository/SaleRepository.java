package com.trendgauge.repository;

import com.trendgauge.model.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    @Query("SELECT SUM(s.amount) FROM SaleEntity s " +
            "WHERE s.storeId = :storeId " +
            "AND s.saleDate BETWEEN :startDate AND :endDate")
    Long sumSales(
            @Param("storeId") Long storeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<SaleEntity> findByStoreIdAndSaleDate(Long storeId, LocalDate saleDate);

    List<SaleEntity> findByStoreIdAndSaleDateBetween(Long storeId, LocalDate startDate, LocalDate endDate);

    List<SaleEntity> findByStoreIdOrderBySaleDateDesc(Long storeId);
}
