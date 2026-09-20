package com.trendgauge.service;

import com.trendgauge.model.entity.ReportEntity;
import com.trendgauge.repository.ReportRepository;
import org.springframework.stereotype.Service;

@Service
public class ReportService {
    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public ReportEntity getReport(Long saleId) {
        return reportRepository.findBySaleId(saleId).orElse(null);
    }
}
