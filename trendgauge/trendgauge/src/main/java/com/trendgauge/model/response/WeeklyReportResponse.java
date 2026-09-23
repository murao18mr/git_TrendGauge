package com.trendgauge.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class WeeklyReportResponse {
    private String storeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long weeklySales;
    private BigDecimal weeklyBudgetRatio;
    private BigDecimal weeklyRatio;
    private String topCategory;

    public WeeklyReportResponse(String storeName, LocalDate startDate, LocalDate endDate, Long weeklySales, BigDecimal weeklyBudgetRatio, BigDecimal weeklyRatio, String topCategory) {
        this.storeName = storeName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.weeklySales = weeklySales;
        this.weeklyBudgetRatio = weeklyBudgetRatio;
        this.weeklyRatio = weeklyRatio;
        this.topCategory = topCategory;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getWeeklySales() {
        return weeklySales;
    }

    public void setWeeklySales(Long weeklySales) {
        this.weeklySales = weeklySales;
    }

    public BigDecimal getWeeklyBudgetRatio() {
        return weeklyBudgetRatio;
    }

    public void setWeeklyBudgetRatio(BigDecimal weeklyBudgetRatio) {
        this.weeklyBudgetRatio = weeklyBudgetRatio;
    }

    public BigDecimal getWeeklyRatio() {
        return weeklyRatio;
    }

    public void setWeeklyRatio(BigDecimal weeklyRatio) {
        this.weeklyRatio = weeklyRatio;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }
}
