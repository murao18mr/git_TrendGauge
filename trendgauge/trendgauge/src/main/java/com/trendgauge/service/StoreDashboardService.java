package com.trendgauge.service;

import com.trendgauge.model.entity.ItemEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.entity.StoreEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.response.CategorySalesResponse;
import com.trendgauge.model.response.ColorSalesResponse;
import com.trendgauge.model.response.SalesTrendResponse;
import com.trendgauge.model.response.StoreRankingResponse;
import com.trendgauge.repository.ItemRepository;
import com.trendgauge.repository.SaleRepository;
import com.trendgauge.repository.StoreRepository;
import com.trendgauge.repository.TargetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class StoreDashboardService {
    private final SaleRepository saleRepository;
    private final StoreRepository storeRepository;
    private final TargetRepository targetRepository;
    private final ItemRepository itemRepository;

    public StoreDashboardService(SaleRepository saleRepository, StoreRepository storeRepository, TargetRepository targetRepository, ItemRepository itemRepository){
        this.saleRepository = saleRepository;
        this.storeRepository = storeRepository;
        this.targetRepository = targetRepository;
        this.itemRepository = itemRepository;
    }

    public Long getSales(Long storeId, LocalDate startDate, LocalDate endDate){
        return saleRepository.sumSales(storeId, startDate, endDate);
    }

    public Long getStoreId(String storeCode){
        Optional<StoreEntity> store = storeRepository.findByStoreCode(storeCode);
        if (store.isEmpty()) {
            return null;
        }
        return store.get().getId();
    }

    public Long getCompanyId(String storeCode){
        Optional<StoreEntity> store = storeRepository.findByStoreCode(storeCode);
        if (store.isEmpty()) {
            return null;
        }
        return store.get().getCompanyId();
    }

    public StoreEntity getStore(String storeCode){
        return storeRepository.findByStoreCode(storeCode).orElse(null);
    }

    public BigDecimal calculateRatio(Long storeId){
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());

        LocalDate lastYear = today.minusYears(1);
        LocalDate lastYearStartDate = lastYear.withDayOfMonth(1);
        LocalDate lastYearEndDate = lastYear.withDayOfMonth(lastYear.lengthOfMonth());

        Long currentSales = getSales(storeId, startDate, endDate);
        Long lastYearSales = getSales(storeId, lastYearStartDate, lastYearEndDate);

        if (currentSales == null) {
            currentSales = 0L;
        }
        if (lastYearSales == null) {
            lastYearSales = 0L;
        }

        BigDecimal currentAmount = BigDecimal.valueOf(currentSales);
        BigDecimal previousAmount = BigDecimal.valueOf(lastYearSales);

        if (previousAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal ratio = currentAmount
                .divide(previousAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return ratio;
    }


    public BigDecimal calculateBudgetRatio(Long storeId){
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.withDayOfMonth(1);
        LocalDate endDate = today.withDayOfMonth(today.lengthOfMonth());
        Long currentSales = getSales(storeId, startDate, endDate);

        if (currentSales == null) {
            currentSales = 0L;
        }

        String targetMonth = today.getYear()
                + "-"
                + String.format("%02d", today.getMonthValue());

        Optional<TargetEntity> target = targetRepository.findByStoreIdAndTargetMonth(storeId, targetMonth);

        if (target.isEmpty() || target.get().getTargetAmount() == null) {
            return null;
        }
        Long targetAmount = target.get().getTargetAmount();
        if (targetAmount == 0L) {
            return null;
        }
        BigDecimal salesAmount = BigDecimal.valueOf(currentSales);
        BigDecimal targetAmountDecimal = BigDecimal.valueOf(targetAmount);

        BigDecimal ratio = salesAmount
                .divide(targetAmountDecimal, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return ratio;
    }

    public List<StoreRankingResponse> getRatioRanking(Long companyId){
        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);
        List<StoreRankingResponse> ranking = new ArrayList<>();

        for(StoreEntity store : stores){
            Long storeId = store.getId();
            BigDecimal ratio = calculateRatio(storeId);

            StoreRankingResponse response = new StoreRankingResponse(storeId, store.getStoreName(), ratio);
            ranking.add(response);
        }
        ranking.sort((a, b) -> {
            if (a.getRatio() == null) {
                return 1;
            }
            if (b.getRatio() == null) {
                return -1;
            }
            return b.getRatio().compareTo(a.getRatio());
        });
        return ranking;
    }

    public List<StoreRankingResponse> getBudgetRanking(Long companyId){
        List<StoreEntity> stores = storeRepository.findByCompanyId(companyId);
        List<StoreRankingResponse> ranking = new ArrayList<>();
        for (StoreEntity store : stores) {
            Long storeId = store.getId();
            BigDecimal ratio = calculateBudgetRatio(storeId);

            StoreRankingResponse response = new StoreRankingResponse(storeId, store.getStoreName(), ratio);
            ranking.add(response);
        }
        ranking.sort((a, b) -> {
            if (a.getRatio() == null) {
                return 1;
            }
            if (b.getRatio() == null) {
                return -1;
            }
            return b.getRatio().compareTo(a.getRatio());
        });
        return ranking;
    }

    public List<SalesTrendResponse> getWeeklySales(Long storeId, LocalDate baseDate){
        LocalDate startDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);

        List<SaleEntity> sales = saleRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate);
        List<SalesTrendResponse> weeklySales = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Long amount = 0L;
            for (SaleEntity sale : sales) {
                if (date.equals(sale.getSaleDate())) {
                    if (sale.getAmount() != null) {
                        amount = sale.getAmount();
                    }
                    break;
                }
            }
            weeklySales.add(new SalesTrendResponse(date, amount));
        }

        return weeklySales;
    }

    public List<CategorySalesResponse> getCategorySales(Long storeId, LocalDate baseDate){
        LocalDate startDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);

        List<ItemEntity> items = itemRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate);

        Map<String, Long> categoryTotals = new HashMap<>();

        for (ItemEntity item : items) {
            String categoryName = item.getCategoryName();
            Long subtotal = item.getSubtotal();
            if (subtotal == null) {
                subtotal = 0L;
            }
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0L) + subtotal);
        }

        List<CategorySalesResponse> categorySales = new ArrayList<>();
        for (Map.Entry<String, Long> entry : categoryTotals.entrySet()) {
            categorySales.add(new CategorySalesResponse(entry.getKey(), entry.getValue()));
        }
        return categorySales;
    }

    public List<ColorSalesResponse> getColorSales(Long storeId, LocalDate baseDate){
        LocalDate startDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDate = startDate.plusDays(6);

        List<ItemEntity> items = itemRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate);

        Map<String, Long> colorTotals = new HashMap<>();

        for (ItemEntity item : items) {
            String colorName = item.getColorName();
            Long subtotal = item.getSubtotal();
            if (subtotal == null) {
                subtotal = 0L;
            }
            colorTotals.put(colorName, colorTotals.getOrDefault(colorName, 0L) + subtotal);
        }

        List<ColorSalesResponse> colorSales = new ArrayList<>();
        for (Map.Entry<String, Long> entry : colorTotals.entrySet()) {
            colorSales.add(new ColorSalesResponse(entry.getKey(), entry.getValue()));
        }
        return colorSales;
    }

    public Long dailyTarget(Long storeId){
        LocalDate today = LocalDate.now();
        Optional<SaleEntity> sale = saleRepository.findByStoreIdAndSaleDate(storeId, today);
        if(sale.isEmpty()){
            return null;
        }
        return sale.get().getTargetAmount();
    }

}
