package com.trendgauge.service;

import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.response.AdminRankingResponse;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.TargetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AdminRankingService {
    private final StoreDashboardService storeDashboardService;
    private final StoreRepository storeRepository;
    private final TargetRepository targetRepository;

    public AdminRankingService(StoreDashboardService storeDashboardService, StoreRepository storeRepository, TargetRepository targetRepository) {
        this.storeDashboardService = storeDashboardService;
        this.storeRepository = storeRepository;
        this.targetRepository = targetRepository;
    }

    public Long getMonthlySales(Long storeId, String targetMonth) {
        YearMonth yearMonth = YearMonth.parse(targetMonth);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return storeDashboardService.getSales(storeId, startDate, endDate);
    }

    public List<StoreEntity> getCompanyStores(Long companyId){
        return storeRepository.findByCompanyId(companyId);
    }

    public Long getMonthlyTarget(Long storeId, String targetMonth) {
        Optional<TargetEntity> target = targetRepository.findByStoreIdAndTargetMonth(storeId, targetMonth);

        if (target.isEmpty() || target.get().getTargetAmount() == null) {
            return null;
        }

        return target.get().getTargetAmount();
    }

    public BigDecimal calculateBudgetRatio(Long storeId, String targetMonth) {
        Long sales = getMonthlySales(storeId, targetMonth);
        Long target = getMonthlyTarget(storeId, targetMonth);

        if (sales == null) {
            sales = 0L;
        }

        if (target == null || target == 0L) {
            return null;
        }

        return BigDecimal.valueOf(sales)
                .divide(BigDecimal.valueOf(target), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal calculateRatio(Long storeId, String targetMonth) {
        YearMonth yearMonth = YearMonth.parse(targetMonth);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        YearMonth lastYear = yearMonth.minusYears(1);

        LocalDate lastYearStartDate = lastYear.atDay(1);
        LocalDate lastYearEndDate = lastYear.atEndOfMonth();

        Long currentSales = storeDashboardService.getSales(storeId, startDate, endDate);
        Long lastYearSales = storeDashboardService.getSales(storeId, lastYearStartDate, lastYearEndDate);

        if (currentSales == null) {
            currentSales = 0L;
        }

        if (lastYearSales == null) {
            lastYearSales = 0L;
        }

        if (lastYearSales == 0L) {
            return null;
        }

        return BigDecimal.valueOf(currentSales)
                .divide(BigDecimal.valueOf(lastYearSales), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public List<AdminRankingResponse> getRanking(Long companyId, String targetMonth, String sortOrder) {
        List<StoreEntity> stores = getCompanyStores(companyId);
        List<AdminRankingResponse> ranking = new ArrayList<>();

        for (StoreEntity store : stores) {
            Long storeId = store.getId();

            Long sales = getMonthlySales(storeId, targetMonth);
            Long targetAmount = getMonthlyTarget(storeId, targetMonth);
            BigDecimal budgetRatio = calculateBudgetRatio(storeId, targetMonth);
            BigDecimal ratio = calculateRatio(storeId, targetMonth);

            AdminRankingResponse response = new AdminRankingResponse(
                    storeId,
                    store.getStoreName(),
                    sales,
                    targetAmount,
                    budgetRatio,
                    ratio
            );

            ranking.add(response);
        }
        if ("budget".equals(sortOrder)) {
            ranking.sort(Comparator.comparing(AdminRankingResponse::getBudgetRatio,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        } else if ("ratio".equals(sortOrder)) {
            ranking.sort(Comparator.comparing(AdminRankingResponse::getRatio,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        } else {
            ranking.sort(Comparator.comparing(AdminRankingResponse::getSales,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }
        return ranking;
    }
}
