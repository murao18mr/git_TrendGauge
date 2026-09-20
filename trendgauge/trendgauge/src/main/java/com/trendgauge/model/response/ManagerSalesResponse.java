package com.trendgauge.model.response;

import com.trendgauge.model.entity.MemoEntity;
import com.trendgauge.model.entity.ReportEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ManagerSalesResponse {
    private Long saleId;
    private LocalDate saleDate;
    private Long amount;
    private Integer customerCount;
    private BigDecimal unitPrice;
    private String weather;
    private ReportEntity report;
    private List<MemoEntity> memos;

    public ManagerSalesResponse(Long saleId, LocalDate saleDate, Long amount, Integer customerCount, BigDecimal unitPrice, String weather, ReportEntity report, List<MemoEntity> memos) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.amount = amount;
        this.customerCount = customerCount;
        this.unitPrice = unitPrice;
        this.weather = weather;
        this.report = report;
        this.memos = memos;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public ReportEntity getReport() {
        return report;
    }

    public void setReport(ReportEntity report) {
        this.report = report;
    }

    public List<MemoEntity> getMemos() {
        return memos;
    }

    public void setMemos(List<MemoEntity> memos) {
        this.memos = memos;
    }

}
