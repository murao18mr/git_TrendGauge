package com.trendgauge.service;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.request.MemoEditInput;
import com.trendgauge.model.request.SalesEditInput;
import com.trendgauge.model.response.KeywordResponse;
import com.trendgauge.model.response.KeywordResultResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static final List<String> KEYWORDS = List.of(
            "接客", "新商品", "セール", "サイズ", "在庫",
            "試着", "人気", "入荷", "予約", "欠品"
    );

    public List<KeywordResponse> getKeywords(String storeCode){
        Long storeId = storeDashboardService.getStoreId(storeCode);
        List<SaleEntity> sales = saleRepository.findByStoreIdOrderBySaleDateDesc(storeId);

        Map<String, Integer> counts = new HashMap<>();

        for (String keyword : KEYWORDS) {
            counts.put(keyword, 0);
        }

        for (SaleEntity sale : sales) {
            ReportEntity report = reportRepository.findBySaleId(sale.getId()).orElse(null);

            if (report != null && report.getSummary() != null) {
                for (String keyword : KEYWORDS) {
                    if (report.getSummary().contains(keyword)) {
                        counts.put(keyword, counts.get(keyword) + 1);
                    }
                }
            }

            List<MemoEntity> memos = memoService.getMemos(sale.getId());

            for (MemoEntity memo : memos) {
                if (memo.getComment() != null) {
                    for (String keyword : KEYWORDS) {
                        if (memo.getComment().contains(keyword)) {
                            counts.put(keyword, counts.get(keyword) + 1);
                        }
                    }
                }
            }
        }

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> new KeywordResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<KeywordResultResponse> searchKeyword(String storeCode, String keyword) {
        Long storeId = storeDashboardService.getStoreId(storeCode);
        List<SaleEntity> sales = saleRepository.findByStoreIdOrderBySaleDateDesc(storeId);
        List<KeywordResultResponse> results = new ArrayList<>();

        for (SaleEntity sale : sales) {
            ReportEntity report = reportRepository.findBySaleId(sale.getId()).orElse(null);

            if (report != null && report.getSummary() != null && report.getSummary().contains(keyword)) {
                results.add(new KeywordResultResponse(
                        sale.getSaleDate(),
                        sale.getAmount(),
                        sale.getWeather(),
                        report.getSummary()
                ));
            }

            List<MemoEntity> memos = memoService.getMemos(sale.getId());

            for (MemoEntity memo : memos) {
                if (memo.getComment() != null && memo.getComment().contains(keyword)) {
                    results.add(new KeywordResultResponse(
                            sale.getSaleDate(),
                            sale.getAmount(),
                            sale.getWeather(),
                            memo.getComment()
                    ));
                }
            }
        }

        return results;
    }
}
