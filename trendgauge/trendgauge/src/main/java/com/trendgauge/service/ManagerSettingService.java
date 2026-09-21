package com.trendgauge.service;

import com.trendgauge.model.entity.TargetEntity;
import com.trendgauge.model.request.TargetInput;
import com.trendgauge.repository.SaleRepository;
import com.trendgauge.repository.TargetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

@Service
public class ManagerSettingService {
    private final TargetRepository targetRepository;
    private final SaleRepository saleRepository;

    public ManagerSettingService(TargetRepository targetRepository, SaleRepository saleRepository) {
        this.targetRepository = targetRepository;
        this.saleRepository = saleRepository;
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
}
