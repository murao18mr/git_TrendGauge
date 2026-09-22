package com.trendgauge.model.request;

import java.time.LocalDate;

public class DailyTargetInput {
    private LocalDate saleDate;
    private Long targetAmount;

    public DailyTargetInput() {
    }

    public DailyTargetInput(LocalDate saleDate, Long targetAmount) {
        this.saleDate = saleDate;
        this.targetAmount = targetAmount;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Long getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(Long targetAmount) {
        this.targetAmount = targetAmount;
    }
}
