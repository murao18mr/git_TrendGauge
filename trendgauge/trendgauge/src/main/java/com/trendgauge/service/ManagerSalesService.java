package com.trendgauge.service;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.request.MemoEditInput;
import com.trendgauge.model.request.SalesEditInput;
import com.trendgauge.model.response.ManagerSalesResponse;
import com.trendgauge.repository.MemoRepository;
import com.trendgauge.repository.ReportRepository;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerSalesService {
    private final SaleRepository saleRepository;
    private final MemoService memoService;
    private final StoreDashboardService storeDashboardService;
    private final ReportRepository reportRepository;
    private final MemoRepository memoRepository;

    public ManagerSalesService(SaleRepository saleRepository, MemoService memoService, StoreDashboardService storeDashboardService, ReportRepository reportRepository, MemoRepository memoRepository) {
        this.saleRepository = saleRepository;
        this.memoService = memoService;
        this.storeDashboardService = storeDashboardService;
        this.reportRepository = reportRepository;
        this.memoRepository = memoRepository;
    }

    public List<ManagerSalesResponse> getSales(String storeCode) {
        Long storeId = storeDashboardService.getStoreId(storeCode);
        List<SaleEntity> sales = saleRepository.findByStoreIdOrderBySaleDateDesc(storeId);
        List<ManagerSalesResponse> responses = new ArrayList<>();

        for (SaleEntity sale : sales) {
            BigDecimal unitPrice = null;

            if (sale.getAmount() != null && sale.getCustomerCount() != null && sale.getCustomerCount() > 0) {
                unitPrice = BigDecimal.valueOf(sale.getAmount())
                        .divide(BigDecimal.valueOf(sale.getCustomerCount()), 0, RoundingMode.HALF_UP);
            }
            ReportEntity report = reportRepository.findBySaleId(sale.getId()).orElse(null);

            responses.add(new ManagerSalesResponse(
                    sale.getId(),
                    sale.getSaleDate(),
                    sale.getAmount(),
                    sale.getCustomerCount(),
                    unitPrice,
                    sale.getWeather(),
                    report,
                    memoService.getMemos(sale.getId())
            ));
        }

        return responses;
    }

    @Transactional
    public void updateSale(String storeCode, SalesEditInput input) {
        Long storeId = storeDashboardService.getStoreId(storeCode);

        SaleEntity sale = saleRepository.findById(input.getSaleId())
                .orElseThrow();

        if (!sale.getStoreId().equals(storeId)) {
            throw new IllegalStateException("この売上データを更新する権限がありません。");
        }

        sale.setAmount(input.getAmount());
        sale.setCustomerCount(input.getCustomerCount());
        sale.setWeather(input.getWeather());
        sale.setUpdatedAt(LocalDateTime.now());
        saleRepository.save(sale);

        ReportEntity report = reportRepository.findBySaleId(sale.getId()).orElse(null);
        String reportText = input.getReport();

        if (reportText == null || reportText.isBlank()) {
            if (report != null) {
                reportRepository.delete(report);
            }
        } else {
            if (report == null) {
                report = new ReportEntity();
                report.setSaleId(sale.getId());
                report.setCreatedAt(LocalDateTime.now());
            }
            report.setSummary(reportText);
            report.setUpdatedAt(LocalDateTime.now());
            reportRepository.save(report);
        }

        for (MemoEditInput memoInput : input.getMemos()) {
            String comment = memoInput.getComment();

            if (memoInput.getMemoId() == null) {
                if (comment != null && !comment.isBlank()) {
                    MemoEntity memo = new MemoEntity();
                    memo.setSaleId(sale.getId());
                    memo.setComment(comment);
                    memo.setCreatedAt(LocalDateTime.now());
                    memo.setUpdatedAt(LocalDateTime.now());
                    memoRepository.save(memo);
                }
            } else {
                MemoEntity memo = memoRepository.findById(memoInput.getMemoId())
                        .orElseThrow();

                if (!memo.getSaleId().equals(sale.getId())) {
                    throw new IllegalStateException("このメモを更新する権限がありません。");
                }

                if (comment == null || comment.isBlank()) {
                    memoRepository.delete(memo);
                } else {
                    memo.setComment(comment);
                    memo.setUpdatedAt(LocalDateTime.now());
                    memoRepository.save(memo);
                }
            }
        }
    }
}
