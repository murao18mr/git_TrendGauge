package com.trendgauge.service;

import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.request.DailyTargetInput;
import com.trendgauge.model.request.TargetInput;
import com.trendgauge.repository.SaleRepository;
import com.trendgauge.repository.TargetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ManagerSettingService {
    private final TargetRepository targetRepository;
    private final SaleRepository saleRepository;
    private final SaleService saleService;

    public ManagerSettingService(TargetRepository targetRepository, SaleRepository saleRepository, SaleService saleService) {
        this.targetRepository = targetRepository;
        this.saleRepository = saleRepository;
        this.saleService = saleService;
    }

    public Optional<TargetEntity> getTarget(Long storeId, String targetMonth) {
        return targetRepository.findByStoreIdAndTargetMonth(storeId, targetMonth);
    }

    public void saveTarget(Long storeId, TargetInput input) {
        Optional<TargetEntity> existingTarget =
                targetRepository.findByStoreIdAndTargetMonth(storeId, input.getTargetMonth());

        if (existingTarget.isPresent()) {
            TargetEntity target = existingTarget.get();
            target.setTargetAmount(input.getTargetAmount());
            target.setTargetRatio(input.getTargetRatio());
            target.setUpdatedAt(LocalDateTime.now());
            targetRepository.save(target);
            return;
        }

        TargetEntity target = new TargetEntity();
        target.setStoreId(storeId);
        target.setTargetMonth(input.getTargetMonth());
        target.setTargetAmount(input.getTargetAmount());
        target.setTargetRatio(input.getTargetRatio());
        target.setCreatedAt(LocalDateTime.now());
        target.setUpdatedAt(LocalDateTime.now());
        targetRepository.save(target);
    }

    public BigDecimal calculateTargetRatio(Long storeId, String targetMonth, Long targetAmount) {
        if (targetAmount == null) {
            return null;
        }

        YearMonth month = YearMonth.parse(targetMonth);
        YearMonth lastYearMonth = month.minusYears(1);

        LocalDate startDate = lastYearMonth.atDay(1);
        LocalDate endDate = lastYearMonth.atEndOfMonth();

        Long lastYearSales = saleRepository.sumSales(storeId, startDate, endDate);

        if (lastYearSales == null || lastYearSales == 0L) {
            return null;
        }

        return BigDecimal.valueOf(targetAmount)
                .divide(BigDecimal.valueOf(lastYearSales), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    @Transactional
    public void saveDailyTargets(Long storeId, List<DailyTargetInput> inputs) {
        for (DailyTargetInput input : inputs) {
            SaleEntity sale = saleService.getOrCreateSale(storeId, input.getSaleDate());
            sale.setTargetAmount(input.getTargetAmount());
            sale.setUpdatedAt(LocalDateTime.now());
            saleRepository.save(sale);
        }
    }

    public List<DailyTargetInput> getDailyTargets(Long storeId, String targetMonth) {
        YearMonth month = YearMonth.parse(targetMonth);
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        List<SaleEntity> sales =
                saleRepository.findByStoreIdAndSaleDateBetween(storeId, startDate, endDate);

        List<DailyTargetInput> inputs = new ArrayList<>();

        for (SaleEntity sale : sales) {
            if (sale.getTargetAmount() != null && sale.getTargetAmount() > 0) {
                inputs.add(new DailyTargetInput(sale.getSaleDate(), sale.getTargetAmount()));
            }
        }

        return inputs;
    }
}
