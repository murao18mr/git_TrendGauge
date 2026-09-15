package com.trendgauge.model.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class DailyReportInput {
    @NotNull(message = "日付は必須入力です")
    private LocalDate saleDate;

    @NotNull(message = "金額は必須入力です")
    private Long salesAmount;

    @NotNull(message = "客数は必須入力です")
    private Integer customerCount;

    private String weather;

    private String summary;

    public DailyReportInput() {
    }

    public DailyReportInput(LocalDate saleDate, Long salesAmount, Integer customerCount, String weather, String summary) {
        this.saleDate = saleDate;
        this.salesAmount = salesAmount;
        this.customerCount = customerCount;
        this.weather = weather;
        this.summary = summary;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public Long getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(Long salesAmount) {
        this.salesAmount = salesAmount;
    }

    public Integer getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(Integer customerCount) {
        this.customerCount = customerCount;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
