package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.response.StoreRankingResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.TargetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AdminDashboardService {
    private final StoreDashboardService storeDashboardService;
    private final StoreRepository storeRepository;
    private final TargetRepository targetRepository;

    public AdminDashboardService(StoreDashboardService storeDashboardService, StoreRepository storeRepository, TargetRepository targetRepository){
        this.storeDashboardService = storeDashboardService;
        this.storeRepository = storeRepository;
        this.targetRepository = targetRepository;
    }

    public Long getMonthlyTotalSales(Long companyId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        Long totalSales = 0L;

        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        for (StoreEntity store : stores) {
            Long sales = storeDashboardService.getSales(store.getId(), startDate, endDate);
            if (sales != null) {
                totalSales += sales;
            }
        }

        return totalSales;
    }

    public BigDecimal calculateBudgetRatio(Long companyId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        String targetMonth = today.getYear() + "-" + String.format("%02d", today.getMonthValue());

        Long totalSales = 0L;
        Long totalTarget = 0L;

        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        for (StoreEntity store : stores) {
            Long sales = storeDashboardService.getSales(store.getId(), startDate, endDate);
            if (sales != null) {
                totalSales += sales;
            }

            Optional<TargetEntity> target = targetRepository.findByStoreIdAndTargetMonth(store.getId(), targetMonth);
            if (target.isPresent() && target.get().getTargetAmount() != null) {
                totalTarget += target.get().getTargetAmount();
            }
        }

        if (totalTarget == 0L) return null;

        return BigDecimal.valueOf(totalSales)
                .divide(BigDecimal.valueOf(totalTarget), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }


    public Long getActiveStoreCount(Long companyId) {
        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);

        return stores.stream()
                .filter(store -> "active".equals(store.getStatus()))
                .count();
    }

    public List<StoreRankingResponse> getTopRanking(Long companyId) {
        return storeDashboardService.getBudgetRanking(companyId)
                .stream()
                .limit(3)
                .toList();
    }
}
