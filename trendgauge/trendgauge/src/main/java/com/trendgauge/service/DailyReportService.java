package com.trendgauge.service;

import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.model.entity.SaleEntity;
import com.trendgauge.model.request.DailyReportInput;
import com.trendgauge.repository.ReportRepository;
import com.trendgauge.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DailyReportService {
    private final SaleRepository saleRepository;
    private final ReportRepository reportRepository;
    private final SaleService saleService;

    public DailyReportService(SaleRepository saleRepository, ReportRepository reportRepository, SaleService saleService){
        this.saleRepository = saleRepository;
        this.reportRepository = reportRepository;
        this.saleService = saleService;
    }

    @Transactional
    public void registerDailyReport(Long storeId, DailyReportInput input){
        SaleEntity sale = saleService.getOrCreateSale(storeId, input.getSaleDate());

        sale.setAmount(input.getSalesAmount());
        sale.setCustomerCount(input.getCustomerCount());
        sale.setWeather(input.getWeather());
        sale.setUpdatedAt(LocalDateTime.now());
        SaleEntity savedSale = saleRepository.save(sale);

        ReportEntity report = new ReportEntity();
        report.setSaleId(savedSale.getId());
        report.setSummary(input.getSummary());
        report.setCreatedAt(LocalDateTime.now());
        report.setUpdatedAt(LocalDateTime.now());
        reportRepository.save(report);
    }


}
