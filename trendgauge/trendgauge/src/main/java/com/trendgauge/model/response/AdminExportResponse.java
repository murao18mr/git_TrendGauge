package com.trendgauge.model.response;

import java.time.LocalDate;

public class AdminExportResponse {
    private String storeName;
    private LocalDate saleDate;
    private Long amount;
    private Integer customerCount;

    public AdminExportResponse(String storeName, LocalDate saleDate, Long amount, Integer customerCount) {
        this.storeName = storeName;
        this.saleDate = saleDate;
        this.amount = amount;
        this.customerCount = customerCount;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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
}
