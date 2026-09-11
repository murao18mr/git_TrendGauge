package com.trendgauge.repository;

import com.trendgauge.model.entity.MemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemoRepository extends JpaRepository<MemoEntity, Long> {
    List<MemoEntity> findBySaleIdOrderByCreatedAtAsc(Long saleId);

}
