package com.trendgauge.service;

import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.response.CategorySalesResponse;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ManagerReportService {
    private final StoreDashboardService storeDashboardService;
    private final SaleRepository saleRepository;

    public ManagerReportService(StoreDashboardService storeDashboardService, SaleRepository saleRepository){
        this.storeDashboardService = storeDashboardService;
        this.saleRepository = saleRepository;
    }

    public Long getStoreId(String storeCode){
        return storeDashboardService.getStoreId(storeCode);
    }

    public Long getWeeklySales(Long storeId, LocalDate startDate, LocalDate endDate){
        return storeDashboardService.getSales(storeId, startDate, endDate);
    }

    public String getStoreName(String storeCode){
        StoreEntity store = storeDashboardService.getStore(storeCode);
        if (store == null) return null;
        return store.getStoreName();
    }

    public Long getWeeklyTarget(Long storeId, LocalDate startDate, LocalDate endDate){
        List<SaleEntity> sales = saleRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate);

        long total = 0L;
        for (SaleEntity sale : sales){
            if (sale.getTargetAmount() != null){
                total += sale.getTargetAmount();
            }
        }
        return total;
    }

    public BigDecimal getWeeklyBudgetRatio(Long storeId, LocalDate startDate, LocalDate endDate){
        Long weeklySales = getWeeklySales(storeId, startDate, endDate);
        Long weeklyTarget = getWeeklyTarget(storeId, startDate, endDate);

        if (weeklySales == null) weeklySales = 0L;
        if (weeklyTarget == null || weeklyTarget == 0L) return null;

        return BigDecimal.valueOf(weeklySales)
                .divide(BigDecimal.valueOf(weeklyTarget), 3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getWeeklyRatio(Long storeId, LocalDate startDate, LocalDate endDate){
        Long weeklySales = getWeeklySales(storeId, startDate, endDate);

        LocalDate lastYearStartDate = startDate.minusYears(1);
        LocalDate lastYearEndDate = endDate.minusYears(1);

        Long lastYearSales = getWeeklySales(storeId, lastYearStartDate, lastYearEndDate);

        if (weeklySales == null) weeklySales = 0L;
        if (lastYearSales == null || lastYearSales == 0L) return null;

        return BigDecimal.valueOf(weeklySales)
                .divide(BigDecimal.valueOf(lastYearSales), 3, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public String getTopCategory(Long storeId, LocalDate startDate, LocalDate endDate){
        List<CategorySalesResponse> categorySales =
                storeDashboardService.getCategorySales(storeId, startDate);

        if (categorySales.isEmpty()) return null;

        CategorySalesResponse topCategory = categorySales.get(0);

        for (CategorySalesResponse category : categorySales) {
            Long amount = category.getAmount();
            if (amount != null && amount > topCategory.getAmount()) {
                topCategory = category;
            }
        }

        return topCategory.getCategoryName();
    }

}
